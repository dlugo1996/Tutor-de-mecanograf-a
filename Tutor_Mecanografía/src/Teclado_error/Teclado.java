package Teclado_error;




import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.*;
import java.util.ArrayList;


public class Teclado extends JFrame {
	private static final long serialVersionUID = 1L;
	private JTextArea textoEsperado;
    private JTextArea textoInsertado;
    private ArrayList<String> pangrama;
    private int posicionPangrama = 0;
    private String pangramaActual;
    private int caracterIncorrecto = 0;
    private int caracterTotal = 0;
    private int caracterCorrecto = 0;
    private boolean procesamientoActivo = false;
    private boolean presionarShift = false;
    private String textoEstudiante = "";
    private String mensajeAnterior = null; 
    private int aciertoFraseActual = 0; 
    private int errorFraseActual = 0; 
    private int aciertoAcumulado = 0; 
    private int errorAcumulado = 0; 
    private boolean primeraVez = true; 
    private long inicio = 0; 

    public Teclado() {
        setTitle("Tutor de Mecanografía");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 500);
        setLayout(new BorderLayout());

        cargarPangramasDesdeArchivo(); 

        textoEsperado = new JTextArea();
        textoEsperado.setEditable(false);
        actualizarPangrama();
        add(textoEsperado, BorderLayout.NORTH);

        textoInsertado= new JTextArea();
        textoInsertado.setLineWrap(true);
        textoInsertado.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textoInsertado);
        add(scrollPane, BorderLayout.CENTER);

        JPanel tecladoVirtual = new JPanel(new GridLayout(4, 10));
        add(tecladoVirtual, BorderLayout.SOUTH);

        String[] teclas = {
            "q", "w", "e", "r", "t", "y", "u", "i", "o", "p",
            "a", "s", "d", "f", "g", "h", "j", "k", "l", "enter",
            "shift", "z", "x", "c", "v", "b", "n", "m", "borrar",
            "espacio",
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "0"
        };

        for (String tecla : teclas) {
            JButton boton = new JButton(tecla);
            boton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!procesamientoActivo) {
                        if (tecla.equals("enter")) {
                            procesarTextoTipeado();
                        } else if (tecla.equals("borrar")) {
                            borrarUltimoCaracter();
                        } else if (tecla.equals("shift")) {
                            presionarShift = !presionarShift;
                            actualizarTeclado(tecladoVirtual);
                        } else if (tecla.equals("espacio")) {
                            textoInsertado.append(" ");
                        } else {
                            char caracter = tecla.charAt(0);
                            if (Character.isLetter(caracter)) {
                                if (presionarShift) {
                                    caracter = Character.toUpperCase(caracter);
                                } else {
                                    caracter = Character.toLowerCase(caracter);
                                }
                            }
                            textoInsertado.append(String.valueOf(caracter));
                        }
                    }
                }
            });
            tecladoVirtual.add(boton);
        }

        setFocusable(true);
        requestFocus();

        textoInsertado.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarPrecision();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarPrecision();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarPrecision();
            }
        });
    }

    private void cargarPangramasDesdeArchivo() {
        pangrama = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader("C:\\Users\\User\\git\\repository4\\Tutor_Mecanografía\\src\\Teclado_error\\pangramasLugo.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String linea;

            while ((linea = bufferedReader.readLine()) != null) {
                pangrama.add(linea);
            }

            bufferedReader.close();
        } catch (IOException e) {
            System.err.println("Error no se puede leer el archivo con los pangramas.");
            e.printStackTrace();
        }
    }

    private void actualizarTeclado(JPanel tecladoVirtual) {
        Component[] componentes = tecladoVirtual.getComponents();
        for (Component componente : componentes) {
            if (componente instanceof JButton) {
                JButton boton = (JButton) componente;
                String etiqueta = boton.getText();
                if (etiqueta.length() == 1) {  
                    char caracter = etiqueta.charAt(0);
                    if (Character.isLetter(caracter)) {
                        if (presionarShift) {
                            caracter = Character.toUpperCase(caracter);
                        } else {
                            caracter = Character.toLowerCase(caracter);
                        }
                        boton.setText(String.valueOf(caracter));
                    }
                }
            }
        }
    }

    private void actualizarPangrama() {
        if (posicionPangrama < pangrama.size()) {
            pangramaActual = pangrama.get(posicionPangrama);
            textoEsperado.setText(pangramaActual);
        } else {
            pangramaActual = null;
            textoEsperado.setText("El pangrama esta completado");
        }
    }

    private void procesarTextoTipeado() {
        if (procesamientoActivo) {
            return;
        }

        procesamientoActivo = true;

        String tipeado = textoInsertado.getText();
        if (pangramaActual == null) {
            procesamientoActivo = false;
            return;
        }

        caracterTotal += pangramaActual.length();

        for (int i = 0; i < pangramaActual.length(); i++) {
            if (i < tipeado.length()) {
                char caracterInsertado = tipeado.charAt(i);
                char caracterEsperado = pangramaActual.charAt(i);

                if (caracterInsertado == caracterEsperado) {
                    if (caracterInsertado != ' ') { 
                        caracterCorrecto++;
                        aciertoFraseActual++; 
                    }
                } else {
                    caracterIncorrecto++;
                    errorFraseActual++; 
                }
            } else {
                caracterIncorrecto++;
                errorFraseActual++; 
            }
        }

        mensajeAnterior = pangramaActual; 
        textoEstudiante = tipeado;

        if (primeraVez) {
            aciertoAcumulado = aciertoFraseActual;
            errorAcumulado = errorFraseActual;
            
            primeraVez = false;
        } else {
            aciertoAcumulado += aciertoFraseActual;
            errorAcumulado += errorFraseActual;
        }

        posicionPangrama++;

        if (posicionPangrama < pangrama.size()) {
            pangramaActual = pangrama.get(posicionPangrama);
            textoEsperado.setText(pangramaActual);
        } else {
            pangramaActual = null;
            textoEsperado.setText(" El pangrama esta completado");
        }

        textoInsertado.setText("");
        mostrarResultados();
        aciertoFraseActual = 0; 
        errorFraseActual = 0; 
        procesamientoActivo = false;
    }

    private void borrarUltimoCaracter() {
        String textoInsertadoActual = textoInsertado.getText();
        if (!textoInsertadoActual.isEmpty()) {
            textoInsertado.setText(textoInsertadoActual.substring(0, textoInsertadoActual.length() - 1));
        }
    }

    private void actualizarPrecision() {
        double precision = 0;
        if (caracterTotal > 0) {
            precision = ((double) caracterCorrecto / caracterTotal) * 100;
        }

        System.out.println("Aciertos: " + caracterCorrecto);
        System.out.println("Errores: " + caracterIncorrecto);
        System.out.println("Precisión del usuario: " + precision + "%");
    }

    private void mostrarResultados() {
        if (mensajeAnterior == null) {
            mensajeAnterior = "No hay mensaje anterior.";
        }

        double tiempo = System.currentTimeMillis() - inicio;
        double velocidad = ((caracterCorrecto + caracterIncorrecto) / tiempo) * 60000;
        double precisionUsuario = ((double) caracterCorrecto / caracterTotal) * 100;

        JFrame ventanaResultados = new JFrame("Estos son los resultados");
        ventanaResultados.setSize(400, 300);
        ventanaResultados.setLayout(new BorderLayout());

        JTextArea resultadosTexto = new JTextArea();
        resultadosTexto.setEditable(false);

        StringBuilder mensajeResultado = new StringBuilder();
        mensajeResultado.append("Mensaje Anterior:\n");
        mensajeResultado.append(mensajeAnterior);
        mensajeResultado.append("\n\nTexto Insertado por el estudiante:\n");
        mensajeResultado.append(textoEstudiante);
        mensajeResultado.append(" \n\n Como tutor he reconocido los siguinetes resultados en tu texto ingresado");
        mensajeResultado.append("\n\n Número de aciertos de la frase actual: ");
        mensajeResultado.append(aciertoFraseActual);
        mensajeResultado.append("\n Número de errores de la frase actual: ");
        mensajeResultado.append(errorFraseActual);
        mensajeResultado.append("\n\n Número de aciertos acumulados: ");
        mensajeResultado.append(aciertoAcumulado);
        mensajeResultado.append("\n Número de errores acumulados: ");
        mensajeResultado.append(errorAcumulado);
        mensajeResultado.append("\n\n Tiempo que demoró en escibir: ");
        mensajeResultado.append(velocidad);
        mensajeResultado.append(" Caracteres por minuto");
        mensajeResultado.append("\n Precisión del estudiante al escribir: ");
        mensajeResultado.append(precisionUsuario);
        mensajeResultado.append("%");

        resultadosTexto.setText(mensajeResultado.toString());

        JScrollPane scrollPane = new JScrollPane(resultadosTexto);
        ventanaResultados.add(scrollPane, BorderLayout.CENTER);

        ventanaResultados.setVisible(true);
    }

    public void iniciar() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Teclado teclado = new Teclado();
                teclado.setVisible(true);
            }
        });
    }
}
