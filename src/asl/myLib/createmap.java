// Internal action code for project escape

package myLib;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class createmap extends DefaultInternalAction {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int color = 0;

	@Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
		// get the window title
		
		final Vector<Integer> params = new Vector<>();
		params.add(Integer.parseInt(args[0].toString()));
		params.add(Integer.parseInt(args[1].toString()));
		params.add(Integer.parseInt(args[2].toString()));
		params.add(Integer.parseInt(args[3].toString()));
        
        final JComponent map = new JPanel(new GridLayout(params.get(1), params.get(0)));
        final JComponent type = new JPanel(new GridLayout(6, 1));
        
        final JFrame frame = new JFrame("Create Map");
        frame.setPreferredSize(new Dimension(1024, 576));
        final JFrame buttonsFrame = new JFrame("Controls");
        buttonsFrame.setPreferredSize(new Dimension(250, 250));
        final Vector<JButton> buttons = new Vector<>();
        
        for(int i = 0; i < params.get(0) * params.get(1); i++) {
        	JButton btn = new JButton();
        	
        	btn.addActionListener(new ActionListener() {
            	public void actionPerformed(ActionEvent e) {
            		if(color == 0)
            			((JButton)e.getSource()).setBackground(new JButton().getBackground());
            		if(color == 1)
            			((JButton)e.getSource()).setBackground(Color.BLACK);
            		if(color == 2)
            			((JButton)e.getSource()).setBackground(Color.GREEN);
            		if(color == 3)
            			((JButton)e.getSource()).setBackground(Color.RED);
            		
            		((JButton)e.getSource()).repaint();
                }
            });     	
        	
            btn.setPreferredSize(new Dimension(10, 10));
            map.add(btn);
            buttons.add(btn);
        }
        map.setBorder(new EmptyBorder(4, 8, 4, 8));
        
        final JButton obstacle = new JButton("Obstacle");
        type.add(obstacle);
        
        final JButton door = new JButton("Door");
        type.add(door);
        
        final JButton mainDoor = new JButton("Main Door");
        type.add(mainDoor);
        
        final JButton clear = new JButton("Clear");
        type.add(clear);
        
        final JButton save = new JButton("Save");
        type.add(save);
        
        final JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*.txt", "txt", "text");
        fileChooser.setFileFilter(filter);
        
        final JButton buttonFile = new JButton("Import");
        type.add(buttonFile);
        
        frame.add(map);
        frame.pack();
        frame.setVisible(true);
        
        buttonsFrame.add(type);
        buttonsFrame.pack();
        buttonsFrame.setVisible(true);
        
        obstacle.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		color = 1;
            }
        });
        
        door.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		color = 2;
            }
        });
        
        mainDoor.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		color = 3;
            }
        });
        
        clear.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		color = 0;
            }
        });
        
        buttonFile.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent event) {
        		
        		int result = fileChooser.showOpenDialog(new JFrame());
        		
                if (result == JFileChooser.APPROVE_OPTION) {
                	
                	frame.setVisible(false);
                	
                	FileReader file;
					try {
						file = new FileReader(fileChooser.getSelectedFile());
						BufferedReader br = new BufferedReader(file);
						
						String line = br.readLine();
		    			String[] lineValues = line.split("\\s+");
		    			
		    			map.removeAll();
		    			buttons.clear();
		    			params.removeAllElements();
		    			params.addElement(Integer.parseInt(lineValues[0]));
		    			params.addElement(Integer.parseInt(lineValues[1]));
		    			params.addElement(Integer.parseInt(lineValues[2]));
		    			params.addElement(Integer.parseInt(lineValues[3]));
		    			
		    			map.setLayout(new GridLayout(params.get(1), params.get(0)));
		    			
		    			for(int i = 0; i < params.get(0) * params.get(1); i++) {
		    	        	JButton btn = new JButton();
		    	        	
		    	        	btn.addActionListener(new ActionListener() {
		    	            	public void actionPerformed(ActionEvent e) {
		    	            		if(color == 0)
		    	            			((JButton)e.getSource()).setBackground(new JButton().getBackground());
		    	            		if(color == 1)
		    	            			((JButton)e.getSource()).setBackground(Color.BLACK);
		    	            		if(color == 2)
		    	            			((JButton)e.getSource()).setBackground(Color.GREEN);
		    	            		if(color == 3)
		    	            			((JButton)e.getSource()).setBackground(Color.RED);
		    	            		
		    	            		((JButton)e.getSource()).repaint();
		    	                }
		    	            });     	
		    	        	
		    	            btn.setPreferredSize(new Dimension(10, 10));
		    	            map.add(btn);
		    	            buttons.add(btn);
		    	        }
		    					    			
		    			frame.repaint();
	    		        frame.setVisible(true);
		    			
	        			while ((line = br.readLine()) != null) {
	        				lineValues = line.split("\\s+");
	        				
	        				switch(lineValues[0]) {
	        				case "OBSTACLE":
	        					buttons.get(Integer.parseInt(lineValues[1]) + Integer.parseInt(lineValues[2]) * params.get(0)).setBackground(Color.BLACK);
	        					break;
	        				case "DOOR":
	        					buttons.get(Integer.parseInt(lineValues[1]) + Integer.parseInt(lineValues[2]) * params.get(0)).setBackground(Color.GREEN);
	        					break;
	        				case "MAINDOOR":
	        					buttons.get(Integer.parseInt(lineValues[1]) + Integer.parseInt(lineValues[2]) * params.get(0)).setBackground(Color.RED);
	        					break;
	        				default:
	        					break;
	        				}
	        				buttons.get(Integer.parseInt(lineValues[1]) + Integer.parseInt(lineValues[2]) * params.get(0)).repaint();
	        			}
	        			
	        			br.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }
        });
        
        //add the event listeners
        save.addActionListener(new ActionListener() {       	
            public void actionPerformed(ActionEvent e) {
            	
            	File folder = new File("worldMaps/");
            	File[] listOfFiles = folder.listFiles();
            	int lastMap = 0;

        	    for (int i = 0; i < listOfFiles.length; i++) {
        	      int map = Integer.parseInt(listOfFiles[i].getName().substring(3, listOfFiles[i].getName().indexOf('.')));
        	      if(lastMap < map)
        	    	  lastMap = map;
        	    }
            	
            	PrintWriter writer;
				try {
					writer = new PrintWriter("worldMaps/Map" + (lastMap + 1) + ".txt", "UTF-8");
					
					writer.println(params.get(0) + " " + params.get(1) + " " + params.get(2) + " " + params.get(3));
	            	
	            	for(int i = 0; i < buttons.size(); i++) {
	            		if(buttons.get(i).getBackground().equals(Color.BLACK))
	            			writer.println("OBSTACLE " + i % params.get(0) + " " + i / params.get(0));
	            		if(buttons.get(i).getBackground().equals(Color.GREEN))
	            			writer.println("DOOR " + i % params.get(0) + " " + i / params.get(0));
	            		if(buttons.get(i).getBackground().equals(Color.RED))
	            			writer.println("MAINDOOR " + i % params.get(0) + " " + i / params.get(0));
	            	}
	            		
	            	writer.close();
	            			
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
            }
        });

        return true;
    }
}
