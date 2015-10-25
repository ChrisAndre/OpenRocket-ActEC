package net.sf.openrocket.gui.main;

import net.miginfocom.swing.MigLayout;
import net.sf.openrocket.ActEC.controls.ElementType;
import net.sf.openrocket.ActEC.dispersion.Engine;
import net.sf.openrocket.ActEC.dispersion.Sample;
import net.sf.openrocket.ActEC.dispersion.mutators.Mutator;
import net.sf.openrocket.ActEC.flightcomputer.FlightComputerType;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import net.sf.openrocket.gui.ActEC.dispersion.Display;
import net.sf.openrocket.gui.SpinnerEditor;
import net.sf.openrocket.gui.adaptors.EnumModel;
import net.sf.openrocket.gui.adaptors.IntegerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

/**
 * Created by chris on 8/17/15.
 */

public class TortureTestPanel extends JSplitPane {

    private final OpenRocketDocument document;
    private Engine engine;
    private Display display;

    private JButton run;

    public TortureTestPanel(OpenRocketDocument doc, Engine engine) {
        super(JSplitPane.HORIZONTAL_SPLIT, true);
        this.document = doc;
        this.engine = engine;
        setResizeWeight(0.5);
        setDividerLocation(0.5);
        display = new Display();
        engine.setFlightComputerType(FlightComputerType.BRICKED_COMPUTER);

        run = new JButton("Run");
        run.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                display.purgeSimulations();
                run();
            }
        });

        JPanel panel = new JPanel(new MigLayout(""));

        panel.add(new JLabel("Torture Runs: "));
        IntegerModel num = new IntegerModel(engine, "RunCount", 1);
        JSpinner spin = new JSpinner(num.getSpinnerModel());
        spin.setEditor(new SpinnerEditor(spin));
        panel.add(spin, "growx");

        panel.add(new JLabel("Flight Computer: "));
        JComboBox combo = new JComboBox(new EnumModel<ElementType>(engine, "FlightComputerType"));
        panel.add(combo, "spanx, growx");

        panel.add(run);
        panel.add(new JButton("Show Log"));

        setLeftComponent(panel);
        setRightComponent(display);
    }

    public void run() {
        engine.addSimListener(new Engine.SampleListener() {
            @Override
            public void sampleSimulationComplete(Sample s) {
                display.addSimulation(s.getSimulation());
            }
        });
        try {
            engine.run();
        }
        catch (Exception e) {
            // TODO:
        }
    }
}
