# Part of the A-A-P GUI IDE: Tool class for Gvim

# Copyright (C) 2002-2003 Stichting NLnet Labs
# Permission to copy and use this file is specified in the file COPYING.
# If this file is missing you can find it here: http://www.a-a-p.org/COPYING

import os
import sys
import string
import SocketServer
import random
import thread
import socket
import time

import Tool
import Util

toollist = []       # Added to in Tool.py

# TODO: do this properly with gettext().
def _(x):
    return x

def canDoActions(item, type):
    """Return a dictionary that describes how well this Tool can perform
       actions on ActyItem "item" with type "type"."""
    if not item.node:
        return {}
    if type in [ "c", "cpp", "txt" ]:
        # Gvim is good at editing text, C and C++ files
        return {
                "view": 50,
                "edit": 50,
               }
    return {
            "view": 10,
            "edit": 10,
           }


gvim_prog_name = None

def gvimProgName(topmodel):
    """Return the name of the Gvim program to use."""
    global gvim_prog_name
    if not gvim_prog_name:
        # Get the name of the gvim program from the configuration file.
        import Config
        default_dict = {"GvimTool/program" : "gvim"}
        gvim_prog_name = Config.get_conf_key("GvimTool/program",
                                                        topmodel, default_dict)
    return gvim_prog_name


# We need to check whether gvim has netbeans support.  If it does we can offer
# many more features.
# -1: not checked yet, 0: no, 1: yes
gvim_has_netbeans = -1

def gvimHasNetbeans(topmodel):
    """Check if gvim has netbeans support.  Caches the result."""
    # XXX Also check if "+clientserver" feature is supported.
    #     If not, can only start and forget.
    global gvim_has_netbeans
    if gvim_has_netbeans == -1:
        if os.name == "posix":
            # On Posix we can redirect the output of "gvim --version".  Avoids
            # that a gvim window pops up.
            ok, res = Util.redir_system_int({"MESSAGE": ""},
                          "%s --version" % gvimProgName(topmodel), use_tee = 0)

        else:
            # Use a portable way to get the output of ":version".
            from RecPython import tempfname
            tmpfile = tempfname()
            try:
                os.system('%s -f -c "set nomore" -c "redir >%s" -c ":version" -c q'
                                           % (gvimProgName(topmodel), tmpfile))
                try:
                    res = open(tmpfile).read()
                except StandardError:
                    res = ''
            finally:
                try:
                    os.remove(tmpfile)
                except:
                    pass

        if string.find(res, "+netbeans_intg") > 0:
            gvim_has_netbeans = 1
        else:
            gvim_has_netbeans = 0

    return gvim_has_netbeans

def getProperties(topmodel):
    """Properties that this kind of tool supports."""
    dict = {
            "mdi" : 1,
            "open_url" : [ "http", "ftp", "scp" ],
            "set_position" : 1,
            }
    if gvimHasNetbeans(topmodel):
        dict["start_talk"] = 1
        dict["get_position"] = 1
        dict["show_pc"] = 1
        dict["show_balloon"] = 1
        dict["display_breakpoint"] = 1
        dict["edit_text"] = 1
        dict["edit_report"] = 1
        dict["edit_block"] = 1
    else:
        dict["start_send"] = 1
    return dict

def openItem(item, action, lnum = None, col = None, off = None):
    """Open ActyItem "item" in a newly created Tool."""
    tool = GvimTool("Gvim file editor", item, action,
                                             lnum = lnum, col = col, off = off)
    item.acty.topmodel.toollist.addTool(tool)
    return tool


class GvimTool(Tool.Tool):
    """A Gvim Tool: edit files with Gvim."""
    def __init__(self, *args, **keyw):
        apply(Tool.Tool.__init__, (self,) + args, keyw)
        self.server = None
        self.handler = None
        self.prep_start()

        self.sernumPC = 22      # serial number used for PC annotation
        self.sernumLast = 33    # serial number used for annotations

    def prep_start(self):
        """Prepare for starting Gvim.  Done when the tool is created and when
           Gvim has exited."""
        self.gvim_thread = None
        self.gvim_open = 0      # set when gvim starts, reset when it exits.
        self.gvim_start_time = 0
        self.buffers = {}       # buffers used in Netbeans Gvim tool
        self.lastbuffer = 0

    def gvimRunning(self):
        """Check if Gvim is still running."""
        if self.gvim_open:
            if gvimHasNetbeans(self.topmodel):
                # gvim_open flag is set and reset appropriately.
                return 1
            # No netbeans connection: need to check if the process is still
            # there.
            if Tool.stillRunning(self.gvim_open):
                return 1
            self.gvim_open = 0
        return 0

    def gvimWait(self):
        """Can't send a command to Gvim when it is still starting up. Wait a
           couple of seconds to avoid an error or starting another Gvim."""
        wait = self.gvim_start_time + 2 - time.time()
        if wait > 0:
            time.sleep(wait)


    def getProperties(self):
        """Properties that this tool supports."""
        return getProperties(self.topmodel)

    def openItem(self, item, action, lnum = None, col = None, off = None):
        """Open ActyItem "item" in this Tool.  Not actually done until
        foreground() is called."""
        self.addItem(item)

    def reload(self, item, node = None):
        """Reload the "item", it was changed elsewhere."""
        if not node:
            node = item.node
        if not self.gvimRunning():
            self.foreground(item, node)
        elif gvimHasNetbeans(self.topmodel):
            # TODO: What if the file was changed?
            if node:
                self.currentnode = node
                self.gvimOpenFile(node)
        else:
            cmd = ":if exists('*inputsave')|call inputsave()|endif"
            # XXX escape special characters
            # XXX jump to other window if it's edited there.
            cmd = cmd + ('|hide edit %s' % item.fullName())
            cmd = cmd + "|if exists('*inputsave')|call inputrestore()|endif<CR>"
            self.sendkeys(cmd)

    def goto(self, item, node = None, lnum = None, col = None, off = None):
        """goto position "lnum"/"col" or "off" in "item".""" 
        self.foreground(item, node)
        if gvimHasNetbeans(self.topmodel):
            if item:
                node = item.node
            buf = self.buffers.get(node)
            if buf and self.handler:
                if off:
                    self.handler.sendCmd(buf.nr, "setDot", " %d" % off)
                elif lnum:
                    if not col:
                        col = 0
                    self.handler.sendCmd(buf.nr, "setDot", " %d/%d"
                                                                 % (lnum, col))
        else:
            if off:
                cmd = ("%dgo" % (off + 1))
            else:
                cmd = ''
                if lnum:
                    cmd = cmd + ("%dG" % lnum)
                if col:
                    cmd = cmd + ("%d|" % col)
            self.sendkeys(cmd)

    def showPC(self, item, node = None, lnum = 0, col = 0, off = None,
                                                                     show = 1):
        """Put PC marker at position "lnum"/"col" or "off" in "item".""" 
        if gvimHasNetbeans(self.topmodel) and self.handler:
            if item:
                node = item.node
            buf = self.buffers.get(node)

            # The Netbeans interface has a very strange way of handling
            # annotations.  An arbitrary name can be used to define it, but
            # it's later placed and unplaced by a number, starting at 1 and
            # rising for each anno used in a node (buffer in Vim terms).

            if not buf.annoPCTypenum:
                # First use in this node: define the annotation type.
                buf.annoLastTypenum = buf.annoLastTypenum + 1
                buf.annoPCTypenum = buf.annoLastTypenum

                # Define a sign with line highlighting of a lightblue
                # background.
                # args: typeNum (not actually used)
                #       typeName (only used to define sign)
                #       tooltip (not used)
                #       glyphFile (name of bitmap for sign, optional)
                #       fg      (fg color number or "none" for line highlight)
                #       bg      (bg color number or "none" for line highlight)
                self.handler.sendCmd(buf.nr, "defineAnnoType",
                                           ' 22 "PC" "" "" none %d' % 0xa0a0ff)

            if show:
                self.addAnno(buf, self.sernumPC, buf.annoPCTypenum,
                                                                lnum, col, off)
            else:
                self.removeAnno(buf, self.sernumPC)

    def addAnno(self, buf, sernum, typenum, lnum, col, off):
        """Add an annotation with serial number "sernum", type number "typenum"
           at postion "lnum"/"col" or "off"."""
        if off:
            s = "%d" % off
        else:
            s = "%d/%d" % (lnum, col)
        self.handler.sendCmd(buf.nr, "addAnno", " %d %d %s -1"
                                                        % (sernum, typenum, s))

    def removeAnno(self, buf, sernum):
        """Delete annotation "sernum"."""
        self.handler.sendCmd(buf.nr, "removeAnno", " %d" % sernum)

    def displayBreakpoint(self, what, bp):
        """Update breakpoint "bp" for action "what"."""
        if (bp.item and bp.item.node and self.buffers.get(bp.item.node)
                and self.handler):
            node = bp.item.node
            buf = self.buffers[node]

            if what == "new":
                if bp in buf.breakpoints:
                    # Already in the list, happens when the tool is both in the
                    # "edit" and "view" list of the node.
                    return
                buf.breakpoints.append(bp)
                if not buf.annoBreakETypenum:
                    # First use in this node: define the annotation type.
                    buf.annoLastTypenum = buf.annoLastTypenum + 1
                    buf.annoBreakETypenum = buf.annoLastTypenum
                    buf.annoLastTypenum = buf.annoLastTypenum + 1
                    buf.annoBreakDTypenum = buf.annoLastTypenum

                    # Define a sign with line highlighting of a lightblue
                    # background.
                    # args: typeNum (not actually used)
                    #       typeName (only used to define sign)
                    #       tooltip (not used)
                    #       glyphFile (name of bitmap for sign, optional)
                    #       fg      (fg color number or "none" for line highlight)
                    #       bg      (bg color number or "none" for line highlight)
                    self.handler.sendCmd(buf.nr, "defineAnnoType",
                                     ' 23 "BreakE" "" ">>" none %d' % 0xffa0a0)
                    self.handler.sendCmd(buf.nr, "defineAnnoType",
                                     ' 24 "BreakD" "" ">>" none %d' % 0xb0b0b0)
                self.sernumLast = self.sernumLast + 1
                self.addAnno(buf, self.sernumLast, buf.annoBreakETypenum,
                                                              bp.lnum, 0, None)
                bp.sernum = self.sernumLast

            else:  # "del" or "upd"
                self.removeAnno(buf, bp.sernum)
                if what == "upd":
                    if bp.enable:
                        typenum = buf.annoBreakETypenum
                    else:
                        typenum = buf.annoBreakDTypenum
                    self.addAnno(buf, bp.sernum, typenum, bp.lnum, 0, None)

    def sendkeys(self, cmd):
        """Execute "cmd" in the Gvim server.  Caller should check if gvim is
           still running."""
        if gvimHasNetbeans(self.topmodel):
            # safety check for using sendkeys() with netbeans connection.
            print "ERROR: send keys: '%s'" % cmd
        else:
            self.gvimWait()
            os.system(gvimProgName(self.topmodel)
                    + ' --servername AAPGVIM --remote-send "<C-\\><C-N>'
                                                                   + cmd + '"')

    def escapeStringArg(self, line):
        """Escape special characters in "line" to be send to gvim.  Reverse of
           parseStringArg()."""
        res = ''
        i = 0
        while i < len(line):
            if line[i] == '\n':
                res = res + "\\n"
            elif line[i] == '\t':
                res = res + "\\t"
            elif line[i] == '\r':
                res = res + "\\r"
            elif line[i] == '\\':
                res = res + "\\\\"
            elif line[i] == '"':
                res = res + '\\"'
            else:
                res = res + line[i]
            i = i + 1
        return res

    def close(self, shutdown):
        """Close the tool.
           Return non-zero if closing is OK."""
        if self.gvimRunning():
            self.gvim_open = 0
            if self.gvim_thread:
                # Tell gvim to exit.
                if self.handler:
                    self.handler.sendFunc(0, "saveAndExit")
                    # We don't wait for the answer, Vim may continue to run.
            else:
                # XXX check if there are any changed files in gvim.
                self.sendkeys(":qa<CR>")
            self.topmodel.toollist.delTool(self)
        return 1        # can shutdown

    def foreground(self, item, node = None, lnum = None):
        """Move this Tool to the foreground, with the current activity.
           When "item" is not None edit this item.
           When "node" is not None edit this node."""
        winposarg = ('"+winpos %d %d" "+set lines=30 columns=82" '
                                % (self.topmodel.view.GetSize().width + 10, 0))
        if item:
            node = item.node

        # Start a Vim server on the file.
        if gvimHasNetbeans(self.topmodel):
            self.gvimRunNetbeans(winposarg)
        if self.handler:
            if node:
                self.currentnode = node
                self.gvimOpenFile(node)
            else:
                self.handler.sendCmd(0, "raise")
        else:
            running = self.gvimRunning()
            cmd = "%s -f --servername AAPGVIM " % gvimProgName(self.topmodel)

            # Jump to the node when it's specified, the user may have started
            # editing another file.
            if not running or node:
                # Position the Vim window next to ours and bring it to the
                # front.
                cmd = cmd + winposarg

                if node:
                    if lnum:
                        pos = "+%d " % lnum
                    else:
                        pos = ''
                    cmd = cmd + ('--remote-silent %s"%s"'
                                                      % (pos, node.fullName()))
                if item:
                    self.setCurrentItem(item)
                else:
                    self.currentnode = node
            else:
                cmd = cmd + '--remote-send "<C-\\><C-N>:call foreground()<CR>"'
            if not running:
                self.gvim_start_time = time.time()
                self.gvim_open = Tool.spawn(cmd)
            else:
                self.gvimWait()
                os.system(cmd)

    def gvimRunNetbeans(self, gvimarg):
        """Make sure we have a running Gvim with Netbeans connection."""
        if self.gvim_open:
            return
        # start a new thread to run the server in
        self.gvim_thread = thread.start_new_thread(self.gvimThreadFunc,
                                                                   (gvimarg, ))
        self.gvim_open = 1

        # Wait until Gvim is available and finished starting up.  We cannot
        # send commands before this.
        # When opening gvim fails the other thead sets gvim_open to zero.
        # Timeout after ten seconds.
        countdown = 1000
        while countdown > 0:
            time.sleep(0.01)
            if self.handler or self.gvim_open == 0:
                break
            countdown = countdown - 1

        if not self.handler:
            # XXX error handling
            self.gvim_open = 0
            print "Cannot open Gvim connection!"

    def gvimThreadFunc(self, gvimarg):
        """Function that is run in a separate thread.  It starts the socket
           server and runs gvim."""
        # start the server

        # IANA says that port numbers 49152 through 65335 are for dynamic use.
        # Start a bit higher to avoid clashes too often.
        portnr = 49765
        while not self.server:
            try:
                self.server = SocketServer.TCPServer(("", portnr), GvimHandler)
                self.server.gvimtool = self
            except Exception, e:
                if string.find(str(e), "ddress already in use") < 0:
                    print "Could not start socket server: ", e
                    self.gvim_open = 0
                    return
                # Port number already in use, retry with another port number.
                portnr = portnr + 1

        # start gvim and tell it to use the server
        # use a random password for semi-security (it shows up in the process
        # list...)
        vimrc = os.path.join(self.topmodel.rootdir, "startup.vim")
        self.password = str(random.random())
        Util.async_system([], None,
                '{quiet} %s --servername AAPGVIM %s-nb:localhost:%d:%s -S "%s"'
                                       % (gvimProgName(self.topmodel), gvimarg,
                                                 portnr, self.password, vimrc))

        # handle ONE request
        self.server.handle_request()

        # Gvim was closed, need to restart the server next time.
        self.prep_start()

        # If netbeans doesn't work close the socket server
        if gvim_has_netbeans == 0:
            del self.server
            self.server = None

    def gvimOpenFile(self, node):
        """Open a file in Gvim through the Netbeans connection.
           Caller must make sure the connection exists."""
        if not self.handler:
            return

        # Find the buffer item
        buf = self.buffers.get(node)
        if not buf:
            # Didn't edit this buffer before, need to pass the file name.
            self.lastbuffer = self.lastbuffer + 1
            buf = GvimBuffer(self.lastbuffer)
            self.buffers[node] = buf
            self.handler.sendCmd(buf.nr, "editFile",
                               ' "%s"' % self.escapeStringArg(node.fullName()))

            # We don't want changes to be reported
            self.handler.sendCmd(buf.nr, "stopDocumentListen")
        else:
            self.handler.sendCmd(buf.nr, "setVisible", " T")
            # In case the user switched to another buffer the breakpoints will
            # be set by the callback fileOpened().

    def fileOpened(self, name):
        """Invoked when a fileOpened event was received from Gvim, passed on to
           the main thread."""
        # Let the generic stuff find a node for this name and add the tool to
        # the item.
        node = Tool.toolOpenedFile(self, name)

        # Add a buffer for the node.
        buf = self.buffers.get(node)
        if not buf:
            self.lastbuffer = self.lastbuffer + 1
            buf = GvimBuffer(self.lastbuffer)
            self.buffers[node] = buf
            if self.handler:
                # Let Gvim know which buffer number we use for this file.
                self.handler.sendCmd(buf.nr, "putBufferNumber",
                               ' "%s"' % self.escapeStringArg(node.fullName()))
                # We don't want changes to be reported
                self.handler.sendCmd(buf.nr, "stopDocumentListen")

        # May show breakpoints in the newly opened file.
        Tool.updateBreakpoints(self, node)

    def showBalloon(self, text):
        """Show a balloon in Gvim."""
        # Limit the length to about 2000 characters to avoid the balloon
        # becoming huge.
        if len(text) > 2000:
            text = text[:1000] + '\n...\n' + text[-1000:]
        self.handler.sendCmd(1, "showBalloon", ' "%s"'
                                                  % self.escapeStringArg(text))

    def bufnr2node(self, bufnr):
        """Lookup a Node by its buffer number.  Returns None if not found."""
        for node in self.buffers.keys():
            if self.buffers[node].nr == bufnr:
                return node
        return None


class GvimBuffer:
    """Holds info about a buffer open in a Netbeans Gvim."""
    def __init__(self, nr):
        self.nr = nr
        self.breakpoints = []

        # stuff for annotations
        self.annoLastTypenum = 0    # last typenr, increase when creating one
        self.annoPCTypenum = 0      # typenr for PC
        self.annoBreakETypenum = 0   # typenr for breakpoint enabled
        self.annoBreakDTypenum = 0   # typenr for breakpoint disabled


class GvimHandler(SocketServer.StreamRequestHandler):
    """Class that handles requests from the gvim we started."""


    def handle(self):
        """This function handles events and replies from gvim.
           It reads from the socket that was opened until gvim exits.
           >>> This runs in the thread of the server. <<<"""

        # We need to know about the tool that started us.
        tool = self.server.gvimtool

        # Init the command sequence number.
        self.cmdnr = 0

        # The first line must contain the password!
        line = self.rfile.readline()
        if (len(line) < 6
                or line[:5] != "AUTH "
                or line[5:len(tool.password) + 5] != tool.password):
            print "Wrong password: %s" % line
            self.sendStr("DISCONNECT")
            tool.gvim_open = 0      # let the tool know it failed
            return

        # Tell Vim to exit without waiting.
        self.sendCmd(0, "setExitDelay", " 0")

        #
        # Hang around to handle messages from Gvim.
        #
        while 1:
            try:
                line = self.rfile.readline()
            except (IOError, socket.error), e:
                sys.__stdout__.write("Read error from Gvim: %s\n" % str(e))
                line = ''
            if line == '':      # End of file, socket must have closed.
                break

            # The tool needs to know about this handler, so that it can call
            # sendStr().
            # This is delayed until the startup is done.
            if not tool.handler and string.find(line, "startupDone") > 0:
                tool.handler = self

            # DEBUG
            # sys.__stdout__.write("received: %s" % line)

            # TODO: handle more events and requests
            if string.find(line, ":version=") > 0:
                # The version must be "2.1" or later.
                if string.find(line, '"2') < 0:
                    print "Wrong netbeans interface version!"
                    global gvim_has_netbeans
                    gvim_has_netbeans = 0
                    self.sendStr("DISCONNECT")
                    # Wait for Gvim to exit, otherwise the server socket will
                    # get stuck.

            elif string.find(line, ":fileOpened=") > 0:
                i = string.find(line, ' ')
                name, i = self.parseStringArg(line, i)
                tool.topmodel.postCall(tool.fileOpened, name)

            # Evaluate the balloon text.
            elif string.find(line, ":balloonText=") > 0:
                i = string.find(line, ' ')
                text, i = self.parseStringArg(line, i)
                tool.topmodel.postCall(Tool.debugEvalText, tool, text)

            elif string.find(line, ":keyAtPos=") > 0:
                bufnr, i = self.parseNumberArg(line, 0)
                node = tool.bufnr2node(bufnr)

                i = string.find(line, ' ')
                name, i = self.parseStringArg(line, i)
                off, i = self.parseNumberArg(line, i)
                lnum, col, i = self.parsePosArg(line, i)

                if node and lnum > 0:
                    enable = 1
                    if name == 's':
                        func = Tool.setBreakpoint
                        what = "new"
                    elif name == 'r':
                        func = Tool.setBreakpoint
                        what = "del"
                    elif name == 'e':
                        func = Tool.setBreakpoint
                        what = "upd"
                    elif name == 'd':
                        func = Tool.setBreakpoint
                        what = "upd"
                        enable = 0
                    elif name == 'R':
                        func = Tool.debugCmd
                        what = "run"
                    elif name == 'C':
                        func = Tool.debugCmd
                        what = "cont"
                    elif name == 'S':
                        func = Tool.debugCmd
                        what = "step"
                    elif name == 'N':
                        func = Tool.debugCmd
                        what = "next"
                    elif name == 'F':
                        func = Tool.debugCmd
                        what = "finish"
                    else:
                        func = 0
                    if func:
                        tool.topmodel.postCall(func, what, node,
                                                           enable, lnum = lnum)


        # Let the tool know the handler finished.  Returning from handle()
        # signals the end of running Gvim.
        tool.handler = None

    def sendStr(self, cmd):
        """Send something to gvim.
           >>> This runs in the thread of the tool. <<<"""
        # DEBUG
        # sys.__stdout__.write("sending command '%s' to Gvim\n" % cmd)
        self.wfile.write(cmd + '\n')

    def sendCmd(self, bufnr, cmd, arg = ''):
        """Send a command to gvim.
           If "arg" is given it must start with a space!"""
        self.cmdnr = self.cmdnr + 1
        self.sendStr("%d:%s!%d%s" % (bufnr, cmd, self.cmdnr, arg))

    def sendFunc(self, bufnr, cmd, arg = ''):
        """Send a function call to gvim.  Doesn't wait for the reply.
           If "arg" is given it must start with a space!"""
        self.cmdnr = self.cmdnr + 1
        self.sendStr("%d:%s/%d%s" % (bufnr, cmd, self.cmdnr, arg))

    def parseStringArg(self, line, i):
        """Parse a string argument in double quotes.  Return the string and the
           index just after the closing quote."""
        # Skip leading blanks, check for presence of '"'.
        while line[i] in string.whitespace:
            i = i + 1
        if line[i] != '"':
            return '', i
        i = i + 1

        name = ''
        while i < len(line):
            if line[i] == '"':
                i = i + 1
                break
            if line[i] == '\\':
                # Translate "\x" if known, otherwise remove the backslash.
                i = i + 1
                if line[i] == 'n':
                    name = name + '\n'
                elif line[i] == 't':
                    name = name + '\t'
                elif line[i] == 'r':
                    name = name + '\r'
                else:
                    name = name + line[i]
            else:
                name = name + line[i]
            i = i + 1

        return name, i

    def parseNumberArg(self, line, i):
        """Parse a number argument.
           Return the number and the index just after it."""
        # Skip leading blanks.
        while line[i] in string.whitespace:
            i = i + 1

        e = i
        if e < len(line) and line[e] in "+-":
            e = e + 1
        while e < len(line) and line[e] in string.digits:
            e = e + 1
        if e > i:
            return int(line[i:e]), e

        # error message?
        return -1, e

    def parsePosArg(self, line, i):
        """Parse a "lnum/col" argument.
           Return the lnu, col and the index just after it."""
        lnum, i = self.parseNumberArg(line, i)
        if line[i] == '/':
            col, i = self.parseNumberArg(line, i + 1)
            return lnum, col, i

        # error message?
        return -1, -1, i


# vim: set sw=4 et sts=4 tw=79 fo+=l:

