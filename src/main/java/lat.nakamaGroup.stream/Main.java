package lat.nakamaGroup.stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// Colors
import lat.nakamaGroup.stream.theme.AppColors;

public class Main extends JFrame {

    private static final String API_URL = "https://nakamastream.lat/api/animes";

    private JPanel animeListPanel;
    private JPanel detailsPanel;
    private JLabel nameLabel;
    private JLabel categoryLabel;
    private JLabel statusLabel;
    private JLabel imageLabel;
    private JTextArea animeDetails;
    private List<JsonObject> animeList;

    public Main() {
        setTitle("NakamaStream Info");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Configuración de estilos y temas
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Panel principal con márgenes y diseño
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        add(mainPanel);

        // Encabezado
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(AppColors.BACKGROUND);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230))); // Línea inferior

        // Logo
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/assets/NakamStream.png")); // Ruta del logo
            Image scaledLogo = logoIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH); // Redimensionar logo
            logoLabel.setIcon(new ImageIcon(scaledLogo));
        } catch (Exception e) {
            logoLabel.setText("Logo"); // Fallback si no encuentra la imagen
            logoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            logoLabel.setForeground(AppColors.TEXT_PRIMARY);
        }
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el logo

        // Título
        JLabel header = new JLabel("NakamaStream Info");
        header.setFont(new Font("Arial", Font.BOLD, 24));
        header.setForeground(AppColors.TEXT_PRIMARY);
        header.setBackground(AppColors.BACKGROUND);
        header.setOpaque(true);
        header.setAlignmentX(Component.CENTER_ALIGNMENT); // Centrar el texto

        // Añadir logo y título al panel del encabezado
        headerPanel.add(logoLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Espacio entre el logo y el título
        headerPanel.add(header);

        // Añadir el panel al contenedor principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel central (Lista de animes y detalles)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        // Panel de lista de animes
        animeListPanel = new JPanel();
        animeListPanel.setLayout(new BoxLayout(animeListPanel, BoxLayout.Y_AXIS));
        animeListPanel.setBackground(Color.WHITE);
        JScrollPane listScrollPane = new JScrollPane(animeListPanel);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setLeftComponent(listScrollPane);

        // Panel derecho con imagen y detalles
        detailsPanel = new JPanel(new BorderLayout(10, 10));
        detailsPanel.setBackground(Color.WHITE);
        splitPane.setRightComponent(detailsPanel);

        JPanel imagePanel = new JPanel();
        imagePanel.setLayout(new BoxLayout(imagePanel, BoxLayout.Y_AXIS)); // Apilar verticalmente
        imagePanel.setBackground(AppColors.BACKGROUND);
        imagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        detailsPanel.add(imagePanel, BorderLayout.NORTH);

    // Imagen del anime
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(300, 300)); // Tamaño fijo
        imageLabel.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

    // Opción: Placeholder (Imagen predeterminada)
        try {
            ImageIcon placeholderIcon = new ImageIcon(getClass().getResource("/assets/placeholder.png")); // Ruta del placeholder
            Image scaledPlaceholder = placeholderIcon.getImage().getScaledInstance(300, 300, Image.SCALE_SMOOTH); // Escalar placeholder
            imageLabel.setIcon(new ImageIcon(scaledPlaceholder));
        } catch (Exception e) {
            // Si no se encuentra el placeholder, dejamos la etiqueta vacía
            imageLabel.setIcon(null);
        }

    // Añadir la etiqueta de la imagen al panel
        imagePanel.add(imageLabel);

        // Panel de información
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        detailsPanel.add(infoPanel, BorderLayout.CENTER);

        // Etiquetas de información
        nameLabel = new JLabel("Nombre: ");
        categoryLabel = new JLabel("Categoría: ");
        statusLabel = new JLabel("Estado: ");

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        nameLabel.setFont(labelFont);
        categoryLabel.setFont(labelFont);
        statusLabel.setFont(labelFont);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(categoryLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        infoPanel.add(statusLabel);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Descripción del anime
        animeDetails = new JTextArea();
        animeDetails.setLineWrap(true);
        animeDetails.setWrapStyleWord(true);
        animeDetails.setEditable(false);
        animeDetails.setFont(new Font("Arial", Font.PLAIN, 14));
        animeDetails.setBackground(new Color(248, 249, 250));
        animeDetails.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(animeDetails);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(222, 226, 230)));
        infoPanel.add(scrollPane);

        // Cargar datos desde la API
        loadAnimeData();
    }

    private void loadAnimeData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(API_URL).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonResponse = response.body().string();
                Gson gson = new Gson();
                JsonArray animeArray = gson.fromJson(jsonResponse, JsonArray.class);

                animeList = new ArrayList<>();

                for (int i = 0; i < animeArray.size(); i++) {
                    JsonObject anime = animeArray.get(i).getAsJsonObject();
                    animeList.add(anime);
                    addAnimeToList(anime);
                }

            } else {
                JOptionPane.showMessageDialog(this, "Error al obtener datos de la API: " + response.code(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al conectar con la API: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAnimeToList(JsonObject anime) {
        String animeName = anime.has("name") && !anime.get("name").isJsonNull()
                ? anime.get("name").getAsString()
                : "Desconocido";
        String categoryName = anime.has("category_name") && !anime.get("category_name").isJsonNull()
                ? anime.get("category_name").getAsString()
                : "Categoría desconocida";

        JPanel animePanel = new JPanel();
        animePanel.setLayout(new BoxLayout(animePanel, BoxLayout.Y_AXIS));
        animePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(222, 226, 230))); // Bootstrap border color
        animePanel.setBackground(Color.WHITE);
        animePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel nameLabel = new JLabel(animeName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(new Color(33, 37, 41)); // Bootstrap dark color
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel categoryLabel = new JLabel(categoryName);
        categoryLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        categoryLabel.setForeground(new Color(108, 117, 125)); // Bootstrap secondary color
        categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        animePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        animePanel.add(nameLabel);
        animePanel.add(Box.createRigidArea(new Dimension(0, 2)));
        animePanel.add(categoryLabel);
        animePanel.add(Box.createRigidArea(new Dimension(0, 5)));

        animePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                showAnimeDetails(anime);
            }

            public void mouseEntered(java.awt.event.MouseEvent evt) {
                animePanel.setBackground(new Color(248, 249, 250)); // Bootstrap light gray
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                animePanel.setBackground(Color.WHITE);
            }
        });

        animeListPanel.add(animePanel);
    }

    private void showAnimeDetails(JsonObject anime) {
        String name = anime.has("name") && !anime.get("name").isJsonNull()
                ? anime.get("name").getAsString() : "Desconocido";
        String categoryName = anime.has("category_name") && !anime.get("category_name").isJsonNull()
                ? anime.get("category_name").getAsString() : "Categoría desconocida";
        String status = anime.has("status") && !anime.get("status").isJsonNull()
                ? anime.get("status").getAsString() : "Estado desconocido";
        String imageUrl = anime.has("imageUrl") && !anime.get("imageUrl").isJsonNull()
                ? anime.get("imageUrl").getAsString() : "";
        String description = anime.has("description") && !anime.get("description").isJsonNull()
                ? anime.get("description").getAsString() : "Sin descripción";

        nameLabel.setText("Nombre: " + name);
        categoryLabel.setText("Categoría: " + categoryName);
        statusLabel.setText("Estado: " + status);
        animeDetails.setText(description);

        if (!imageUrl.isEmpty()) {
            imageLabel.setIcon(null);
            imageLabel.setText("Cargando imagen...");

            SwingWorker<ImageIcon, Void> worker = new SwingWorker<>() {
                @Override
                protected ImageIcon doInBackground() throws Exception {
                    URL url = new URL(imageUrl);
                    Image image = ImageIO.read(url);
                    return new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon icon = get();
                        imageLabel.setIcon(icon);
                        imageLabel.setText("");
                    } catch (Exception e) {
                        imageLabel.setIcon(null);
                        imageLabel.setText("No se pudo cargar la imagen");
                    }
                }
            };
            worker.execute();
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("Imagen no disponible");
        }

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            app.setVisible(true);
        });
    }
}
