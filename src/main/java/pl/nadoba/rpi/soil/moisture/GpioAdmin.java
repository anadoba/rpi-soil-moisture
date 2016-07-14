package pl.nadoba.rpi.soil.moisture;

import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.spi.SpiChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GpioAdmin {

    private final static int CHECK_DURATION = 30000;
    private final static int MULTIPLEX_INTERVAL = 7;

    private final GpioController gpio = GpioFactory.getInstance();

    private final GpioPinDigitalOutput digit1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, "DIGIT 1", PinState.LOW);
    private final GpioPinDigitalOutput digit2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, "DIGIT 2", PinState.LOW);
    private final GpioPinDigitalOutput digit3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "DIGIT 3", PinState.LOW);
    private final GpioPinDigitalOutput digit4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_00, "DIGIT 4", PinState.LOW);

    private final GpioPinDigitalOutput shiftDataInput = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "SHIFT DATA INPUT", PinState.LOW);
    private final GpioPinDigitalOutput shiftToggle = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "SHIFT TOGGLE", PinState.LOW);

    private final ShiftClient shiftClient = new ShiftClient(shiftDataInput, shiftToggle);

    private MCP3008GpioProvider mcp3008;

    private String percentage = "1234";

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
        Runnable display = () -> { displayLoop(); };
	Thread displayThread = new Thread(display);
	displayThread.start();

        for (; ; ) {
            String rawPercentage = getPinValuePercentage(MCP3008Pin.CH0);
            System.out.println("MCP3008 CH0: " + rawPercentage + "%");
            percentage = formatPercentage(rawPercentage);
            Thread.sleep(CHECK_DURATION);
        }
    }

    private String formatPercentage(String rawPercentage) {
        while (rawPercentage.length() < 4) {
            rawPercentage = " " + rawPercentage;
        }

        return rawPercentage;
    }

    private void displayLoop() {
        List<GpioPinDigitalOutput> digits = new ArrayList<>(Arrays.asList(digit1, digit3, digit2, digit4));

        for (; ; ) {
            for (int i = 0; i < 4; i++) {
		char targetChar = percentage.charAt(i);
		if (targetChar == ' ') {
			continue;
		}
                	shiftClient.process(targetChar);
                	GpioPinDigitalOutput digit = digits.get(i);
                	digit.high();
			try { Thread.sleep(MULTIPLEX_INTERVAL); } catch (InterruptedException e) {}
                	digit.low();
		
            }
        }
    }

    private String getPinValuePercentage(Pin pin) {
        double d = mcp3008.getValue(pin);
        return OutputNormalizer.toPercent(d);
    }

}
