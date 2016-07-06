package pl.nadoba.rpi.soil.moisture;

import com.pi4j.io.gpio.GpioPinDigitalOutput;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

public class ShiftClient {

    private GpioPinDigitalOutput dataInput;
    private GpioPinDigitalOutput toggle;

    public ShiftClient(GpioPinDigitalOutput dataInput, GpioPinDigitalOutput toggle) {
        this.dataInput = dataInput;
        this.toggle = toggle;
    }

    private void next() {
        toggle.high();
        toggle.low();
    }

    private List<Boolean> digitToCommands(char digit) {
        List<Boolean> states;

        switch (digit) {
            case '0':
                states = Arrays.asList(true, true, true, true, true, true, false, false);
                break;
            case '1':
                states = Arrays.asList(false, true, true, false, false, false, false, false);
                break;
            case '2':
                states = Arrays.asList(true, true, false, true, true, false, true, false);
                break;
            case '3':
                states = Arrays.asList(true, true, true, true, false, false, true, false);
                break;
            case '4':
                states = Arrays.asList(false, true, true, false, false, true, true, false);
                break;
            case '5':
                states = Arrays.asList(true, false, true, true, false, true, true, false);
                break;
            case '6':
                states = Arrays.asList(true, false, true, true, true, true, true, false);
                break;
            case '7':
                states = Arrays.asList(true, true, true, false, false, false, false, false);
                break;
            case '8':
                states = Arrays.asList(true, true, true, true, true, true, true, false);
                break;
            case '9':
                states = Arrays.asList(true, true, true, true, false, true, true, false);
                break;
            default:
                throw new RuntimeException("Input character was not a digit! - got: " + digit);
        }

        if (states.size() != 8)
            throw new RuntimeException("Fix your code, man!");

        return new ArrayList<Boolean>(states);
    }

    public void process(char digit) {
        List<Boolean> states = digitToCommands(digit);
        Collections.reverse(states);
        states.add(true); // we want to add 'false' in the end

        for (Boolean state : states) {
            dataInput.setState(!state);
            next();
        }
    }
}
