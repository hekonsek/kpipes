package net.kpipes.lib.process

final class SudoResolver {

    private SudoResolver() {
    }

    static List<String> resolveSudo(Command command) {
        def commandSegments = command.command()
        def sudoPassword = command.sudoPassword()
        if(command.sudo() && System.getProperty('user.name') != 'root') {
            if(sudoPassword == null) {
                throw new IllegalStateException('Sudo access is required to execute the command. Please set up SUDO_PASSWORD environment variable or JVM system property.')
            } else if(sudoPassword.isEmpty()) {
                commandSegments.add(0, 'sudo')
            } else {
                commandSegments = ['/bin/bash', '-c', "echo '${sudoPassword}'| sudo -S ${commandSegments.join(' ')}".toString()]
            }
        }
        commandSegments
    }

}
