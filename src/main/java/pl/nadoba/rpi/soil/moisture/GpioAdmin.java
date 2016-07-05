package pl.nadoba.rpi.soil.moisture;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;

import java.io.IOException;

public class GpioAdmin {

    private final static int CHECK_DURATION = 2500;

    private final GpioController gpio = GpioFactory.getInstance();

    private final GpioPinDigitalOutput digit1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "DIGIT 1", PinState.LOW);
    private final GpioPinDigitalOutput digit2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "DIGIT 2", PinState.LOW);
    private final GpioPinDigitalOutput digit3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "DIGIT 3", PinState.LOW);
    private final GpioPinDigitalOutput digit4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "DIGIT 4", PinState.LOW);

    private final GpioPinDigitalOutput shiftDataInput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "SHIFT DATA INPUT", PinState.LOW);
    private final GpioPinDigitalOutput shiftToggle = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "SHIFT TOGGLE", PinState.LOW);

    private MCP3008GpioProvider mcp3008 = null;

    public GpioAdmin() {
        try {
            mcp3008 = new MCP3008GpioProvider(SpiChannel.CS0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        init();
    }

    private void init() {
    }

    public void loop() throws InterruptedException {
        for (; ; ) {
            System.out.println("MCP3008 CH0: " + getPinValuePercentage(MCP3008Pin.CH0) + "%");

            Thread.sleep(CHECK_DURATION);
        }
    }

    private String getPinValuePercentage(Pin pin) {
        double d = mcp3008.getValue(pin);
        return OutputNormalizer.toPercent(d);
    }

}
