package net.kpipes.kernel

class Kernel {

    void start() {
        while (true) {
            Thread.sleep(5000)
        }
    }

    public static void main(String[] args) {
        new Kernel().start()
    }

}
