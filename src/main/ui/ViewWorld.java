package ui;

import model.FantasyWorld;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

// A class that represents options inside view-world
public class ViewWorld extends JFrame {

    protected JFrame frame;
    protected JMenuBar menu;
    protected WorldApp myWorld;

    //EFFECTS: construct object to lay out gui of view-world option
    public ViewWorld(WorldApp myWorld) {
        this.myWorld = myWorld;

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(myWorld.getWidth(), myWorld.getHeight());

        menu = new JMenuBar();
        JButton allWorld = new JButton("All Worlds");
        buttonProperties(allWorld);
        allWorld.addActionListener(new AllWorldAction());

        JButton fav = new JButton("Favourites");
        fav.addActionListener(new SubListAction(myWorld.getWorld().getFav()));
        buttonProperties(fav);

        JButton beenTo = new JButton("Been To");
        beenTo.addActionListener(new SubListAction(myWorld.getWorld().getBeenTo()));
        buttonProperties(beenTo);

        JButton wantTo = new JButton("Want To");
        wantTo.addActionListener(new SubListAction(myWorld.getWorld().getWantTo()));
        buttonProperties(wantTo);

        JButton backtoHomePage = new JButton("Home Page");
        backtoHomePage.addActionListener(new HomePageAction());
        buttonProperties(backtoHomePage);

        frame.setJMenuBar(menu);
        frame.setVisible(true);
    }

    public void buttonProperties(JButton button) {
        menu.add(button);
    }

    //a class that represents going back to homepage
    private class HomePageAction implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            new HomePage(myWorld);
        }
    }

    // a class that represents the function of all world button
    private class AllWorldAction implements ActionListener {
        JList list;
        DefaultListModel listModel;
        JButton deleteButton;
        JButton markAsButton;

        @Override
        // EFFECTS: lay the list of all worlds to gui
        // create a panel for each world, with appropriate 2 buttons
        public void actionPerformed(ActionEvent e) {
            frame.getContentPane().removeAll();
            JPanel framePanel = new JPanel();
            framePanel.setLayout(new BorderLayout());

            listModel = new DefaultListModel();
            for (FantasyWorld fw : myWorld.getWorld().getAllWorld()) {
                listModel.addElement(fw.getName());
            }
            list = new JList(listModel);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollList = new JScrollPane(list);

            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            markAsButton = new JButton("Mark As");
            markAsButton.addActionListener(new MarkAsAction());
            deleteButton = new JButton("Delete");
            deleteButton.addActionListener(new DeleteAction());
            buttonPanel.add(markAsButton);
            buttonPanel.add(deleteButton);

            framePanel.add(scrollList, BorderLayout.PAGE_START);
            framePanel.add(buttonPanel, BorderLayout.PAGE_END);
            frame.add(framePanel);
            frame.setVisible(true);
        }

        // a class that presents the markAs action
        private class MarkAsAction implements ActionListener, ItemListener {

            JCheckBox favButton;
            JCheckBox beenToButton;
            JCheckBox wantToButton;
            FantasyWorld chosen;

            // MODIFIES: this
            // EFFECTS: prompt 3 choices of either fav, beenTo, wantTo, then mark the world as chosen state.
            // can choose only one at one time
            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu popupMenu = new JPopupMenu();
                favButton = new JCheckBox("Fav");

                favButton.addItemListener(this);
                beenToButton = new JCheckBox("Been To");
                beenToButton.addItemListener(this);
                wantToButton = new JCheckBox("Want To");
                wantToButton.addItemListener(this);

                popupMenu.add(favButton);
                popupMenu.add(beenToButton);
                popupMenu.add(wantToButton);
                popupMenu.show(markAsButton, markAsButton.getX(), markAsButton.getY());

                int index = list.getSelectedIndex();
                String worldName = (String) listModel.get(index);
                for (FantasyWorld fw : myWorld.getWorld().getAllWorld()) {
                    if (fw.getName().equals(worldName)) {
                        chosen = fw;
                    }
                }
            }

            // MODIFIES: this
            // EFFECTSl based on the option chosen of the checkbox, add the appropriate world to corresponding sublist
            @Override
            public void itemStateChanged(ItemEvent e) {
                Object source = e.getItemSelectable();

                if (source == favButton) {
                    myWorld.getWorld().add(myWorld.getWorld().getFav(), chosen);
                }
                if (source == beenToButton) {
                    myWorld.getWorld().add(myWorld.getWorld().getBeenTo(), chosen);
                }
                if (source == wantToButton) {
                    myWorld.getWorld().add(myWorld.getWorld().getWantTo(), chosen);
                }
            }
        }

        // a class that represents the delete action of delete button
        private class DeleteAction implements ActionListener {

            //MODIFIES: this
            //EFFECTS: delete the selected world from the world state, laying out on the gui.
            // Disable button if list is empty
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
                String worldName = (String) listModel.get(index);
                for (FantasyWorld fw : myWorld.getWorld().getAllWorld()) {
                    if (fw.getName().equals(worldName)) {
                        myWorld.getWorld().deleteWorld(fw);
                    }
                }
                listModel.remove(index);

                if (listModel.size() == 0) {
                    deleteButton.setEnabled(false);
                }
            }
        }
    }

    // a class that represents the function of sublist button
    private class SubListAction implements ActionListener {

        DefaultListModel listModel;
        JList list;
        List<FantasyWorld> sublist;
        JButton removeButton;

        //EFFECTS: construct a sublistaction obj with specified sublist
        public SubListAction(List<FantasyWorld> sublist) {
            this.sublist = sublist;
        }

        @Override
        //EFFECTS: lay the sublists to gui
        public void actionPerformed(ActionEvent e) {
            layout(sublist);
            frame.setVisible(true);
        }

        // EFFECTS: helper method that determines how sublists should be laid out
        public void layout(List<FantasyWorld> sublist) {
            frame.getContentPane().removeAll();
            JPanel panelForFrame = new JPanel();
            panelForFrame.setLayout(new BorderLayout());

            listModel = new DefaultListModel();
            for (FantasyWorld fw : sublist) {
                listModel.addElement(fw.getName());
            }
            list = new JList(listModel);
            list.setLayoutOrientation(JList.VERTICAL);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            JScrollPane scrollList = new JScrollPane(list);
            panelForFrame.add(scrollList, BorderLayout.PAGE_START);

            JPanel buttonPanel = new JPanel();
            removeButton = new JButton("Remove");
            removeButton.addActionListener(new RemoveAction());
            buttonPanel.add(removeButton);
            panelForFrame.add(buttonPanel, BorderLayout.PAGE_END);

            frame.add(panelForFrame);
        }

        // a class representing the remove action for remove buttons
        private class RemoveAction implements ActionListener {

            // MODIFIES: this
            // EFFECTS: remove the world from according sublists when the button is pressed, if listModel is empty
            // then disable the button
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
                String worldName = (String) listModel.get(index);
                for (FantasyWorld fw : sublist) {
                    if (fw.getName().equals(worldName)) {
                        myWorld.getWorld().remove(sublist, fw);
                    }
                }
                listModel.remove(index);

                if (listModel.size() == 0) {
                    removeButton.setEnabled(false);
                }
            }
        }
    }

}

