##Script Henplus Plug-In##

This plugin allows you to execute MVEL scripts in a couple of ways. First, manually with the `script` command. It also can be
configured to execute a script after commands are executed. MVEL is a simple Expression Language which can be used as a
dynamic scripting language.

###Easy Setup###

Simply put `script-henplus-plugin.jar` and `mvel2-2.2.4.Final.jar` in to the CLASSPATH of `henplus`, generally in the `share/henplus` folder somewhere.

Start `henplus` and register the plugin. Use the `plug-in` command for this. This only needs to be done once, and will be persisted.

     Hen*Plus> plug-in org.fakebelieve.henplus.plugins.script.ScriptCommand

###Usage###

When enabled, you can manually execute scripts with the `script` command.

    script <script-name>

There will be two predefined variables from HenPlus you can access

    _session - the current SQL session
    _henplus - the HenPlus instance


You can also create a script called `script.mvel` in `$HOME/.henplus` that will be executed  after each command is completed. There
will be several predefined variables

    _session - the current SQL session
    _command - the full command that was executed
    _result  - the status of the last command
    _henplus - the HenPlus instance

So for example if you want to automatically do something after connections are made, like enabling auto-commit, you can
create a script like

    if (_command.startsWith("connect") && _result == 0) {
      _henplus.msg().println("Enabling auto-commit.");
      _henplus.getDispatcher().execute(_session, "set-session-property auto-commit true");
    };
