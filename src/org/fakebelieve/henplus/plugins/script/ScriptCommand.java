/*
 * This is free software, licensed under the Gnu Public License (GPL)
 * get a copy from <http://www.gnu.org/licenses/gpl.html>
 */
package org.fakebelieve.henplus.plugins.script;

import henplus.*;
import henplus.event.ExecutionListener;
import org.mvel2.MVEL;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ScriptCommand extends AbstractCommand {

    private static final String COMMAND_SCRIPT = "script";
    private static final String COMMAND_LIST = "list-scripts";

    /**
     *
     */
    public ScriptCommand() {
        registerPromptUpdater(HenPlus.getInstance().getDispatcher());
    }

    public void registerPromptUpdater(final CommandDispatcher dispatcher) {
        dispatcher.addExecutionListener(new ExecutionListener() {

            @Override
            public void beforeExecution(final SQLSession session, final String command) {
            }

            @Override
            public void afterExecution(SQLSession currentSession, final String command, final int result) {
                currentSession = HenPlus.getInstance().getCurrentSession();

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("_henplus", HenPlus.getInstance());
                variables.put("_dispatcher", HenPlus.getInstance().getDispatcher());
                variables.put("_msg", HenPlus.msg());
                variables.put("_session", currentSession);
                variables.put("_command", command);
                variables.put("_result", result);

                File file = new File(HenPlus.getInstance().getConfigurationDirectoryInfo(), "script.mvel");
                if (file.exists()) {
                    try {
                        MVEL.evalFile(file, variables);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#getCommandList()
     */
    @Override
    public String[] getCommandList() {
        return new String[]{COMMAND_SCRIPT, COMMAND_LIST};
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#participateInCommandCompletion()
     */
    @Override
    public boolean participateInCommandCompletion() {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#execute(henplus.SQLSession, java.lang.String, java.lang.String)
     */

    @Override
    public int execute(SQLSession session, String command, String parameters) {
        int result = SUCCESS;

        if (command.equals(COMMAND_SCRIPT)) {
            // required: session
            if (session == null) {
                HenPlus.msg().println("You need a valid session for this command.");
                return EXEC_FAILED;
            }

            if (parameters == null || parameters.isEmpty()) {
                HenPlus.msg().println("You need to supply a file to execute");
                return EXEC_FAILED;
            } else {
                parameters = parameters.trim();

                List<String> parameterList = ScriptUtil.parseTokens(parameters);
                String script = parameterList.get(0);
                List<String> arguments = parameterList.subList(1, parameterList.size());

                Map<String, Object> variables = new HashMap<String, Object>();
                variables.put("_henplus", HenPlus.getInstance());
                variables.put("_dispatcher", HenPlus.getInstance().getDispatcher());
                variables.put("_msg", HenPlus.msg());
                variables.put("_session", HenPlus.getInstance().getCurrentSession());
                variables.put("_args", arguments);


                File file = new File(script);
                if (!file.exists()) {
                    file = new File(HenPlus.getInstance().getConfigurationDirectoryInfo(), script);
                }

                if (file.exists()) {
                    try {
                        MVEL.evalFile(file, variables);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    HenPlus.msg().println("Script file \"" + script + "\" doesn't exist");
                }
            }
        }
        else if (command.equals(COMMAND_LIST)) {
            File directory = new File(HenPlus.getInstance().getConfigurationDirectoryInfo());
            String[] scripts = directory.list(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".mvel");
                }
            });
            for (String script : scripts) {
                HenPlus.msg().println("  " + script);
            }
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#isComplete(java.lang.String)
     */
    @Override
    public boolean isComplete(String command) {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#requiresValidSession(java.lang.String)
     */
    @Override
    public boolean requiresValidSession(String cmd) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#shutdown()
     */
    @Override
    public void shutdown() {
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#getShortDescription()
     */
    @Override
    public String getShortDescription() {
        return "execute an MVEL script";
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#getSynopsis(java.lang.String)
     */
    @Override
    public String getSynopsis(String cmd) {
        return COMMAND_SCRIPT + " " + " <script>";
    }

    /*
     * (non-Javadoc)
     * @see henplus.Command#getLongDescription(java.lang.String)
     */
    @Override
    public String getLongDescription(String cmd) {
        return "\tThe \"script\" plug-in works in two modes. After every command\n"
                + "\tit will execute $HOME/.henplus/script.mvel if it exists, with\n"
                + "\tthe following predefined variables\n"
                + "\t\t_session - the current SQL session\n"
                + "\t\t_command - the full command that was executed\n"
                + "\t\t_result  - the status of the last command\n"
                + "\t\t_henplus - the HenPlus instance\n"
                + "\t\t_dispatcher - the HenPlus command dispatcher\n"
                + "\t\t_msg        - the HenPlus message printer\n"
                + "\n"
                + "\tYou can also execute scripts directly with\n"
                + "\t\t" + COMMAND_SCRIPT + " <script>;\n"
                + "\twith the following predefined variables\n"
                + "\t\t_session    - the current SQL session\n"
                + "\t\t_henplus    - the HenPlus instance\n"
                + "\t\t_dispatcher - the HenPlus command dispatcher\n"
                + "\t\t_msg        - the HenPlus message printer\n"
                + "\t\t_args       - the arguments passed to the script\n"
                + "\n";
    }
}
