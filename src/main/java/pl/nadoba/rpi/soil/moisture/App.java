package pl.nadoba.rpi.soil.moisture;

public class App {

    public static void main(String[] args) throws InterruptedException {

        GpioAdmin adc = new GpioAdmin();
        adc.loop();
    }

}
