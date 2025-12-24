package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import com.hust.soict.aims.entities.*;

public class ProductDetailScreen extends JDialog {
    public ProductDetailScreen(Frame owner, Product p) {
        super(owner, "Product details", true);
        setLayout(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);
        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(p.getTitle()).append("\n");
        sb.append("Current price: ").append(p.getCurrentPrice()).append("\n");
        sb.append("Original value: ").append(p.getOriginalValue()).append("\n");
        sb.append("Weight: ").append(p.getWeight()).append("\n");
        sb.append("Dimension: ").append(p.getDimension()).append("\n");
        sb.append("Description: ").append(p.getDescription()).append("\n\n");

        if (p instanceof Book) {
            Book b = (Book) p;
            sb.append("Type: Book\n");
            sb.append("Author: ").append(b.getAuthor()).append("\n");
            sb.append("Cover: ").append(b.getCoverType()).append("\n");
            sb.append("Publisher: ").append(b.getPublisher()).append("\n");
            sb.append("Publication date: ").append(b.getPublicationDate()).append("\n");
            if (b.getNumberOfPages()!=null) sb.append("Pages: ").append(b.getNumberOfPages()).append("\n");
            sb.append("Language: ").append(b.getLanguage()).append("\n");
            sb.append("Genre: ").append(b.getGenre()).append("\n");
        } else if (p instanceof Newspaper) {
            Newspaper n = (Newspaper) p;
            sb.append("Type: Newspaper\n");
            sb.append("Editor in chief: ").append(n.getEditorInChief()).append("\n");
            sb.append("Publisher: ").append(n.getPublisher()).append("\n");
            sb.append("Publication date: ").append(n.getPublicationDate()).append("\n");
            sb.append("Issue number: ").append(n.getIssueNumber()).append("\n");
            sb.append("Frequency: ").append(n.getPublicationFrequency()).append("\n");
            sb.append("ISSN: ").append(n.getIssn()).append("\n");
            sb.append("Language: ").append(n.getLanguage()).append("\n");
            sb.append("Sections: ").append(n.getSections()).append("\n");
        } else if (p instanceof CD) {
            CD c = (CD) p;
            sb.append("Type: CD\n");
            sb.append("Album: ").append(c.getAlbum()).append("\n");
            sb.append("Artist: ").append(c.getArtist()).append("\n");
            sb.append("Record label: ").append(c.getRecordLabel()).append("\n");
            sb.append("Genre: ").append(c.getGenre()).append("\n");
            sb.append("Release date: ").append(c.getReleaseDate()).append("\n");
            List<String> tracks = c.getTrackList();
            if (tracks != null) {
                sb.append("Tracks:\n");
                for (String t: tracks) sb.append(" - ").append(t).append("\n");
            }
        } else if (p instanceof DVD) {
            DVD d = (DVD) p;
            sb.append("Type: DVD\n");
            sb.append("Disc type: ").append(d.getDiscType()).append("\n");
            sb.append("Director: ").append(d.getDirector()).append("\n");
            sb.append("Runtime: ").append(d.getRuntime()).append("\n");
            sb.append("Studio: ").append(d.getStudio()).append("\n");
            sb.append("Language: ").append(d.getLanguage()).append("\n");
            sb.append("Subtitles: ").append(d.getSubtitles()).append("\n");
            sb.append("Release date: ").append(d.getReleaseDate()).append("\n");
            sb.append("Genre: ").append(d.getGenre()).append("\n");
        } else {
            sb.append("Type: Generic product\n");
        }

        area.setText(sb.toString());
        area.setCaretPosition(0);
        add(new JScrollPane(area), BorderLayout.CENTER);
    JButton close = new JButton("Close");
    close.addActionListener(e -> setVisible(false));
    JPanel bottomPanel = new JPanel();
    bottomPanel.add(close);
    add(bottomPanel, BorderLayout.SOUTH);
        setSize(400, 400);
        setLocationRelativeTo(owner);
    }
}
