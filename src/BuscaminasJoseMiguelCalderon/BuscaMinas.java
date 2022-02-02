package BuscaminasJoseMiguelCalderon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class BuscaMinas {
	static JFrame frame = new JFrame();

	public static void main(String[] args) {
		frame.setTitle("Buscaminas");
		BuscaMinas.frame.setSize(400, 470);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		LaminaPrincipal laminaPrincipal = new LaminaPrincipal();
		frame.add(laminaPrincipal);
		frame.setVisible(true);
	}
}

class LaminaPrincipal extends JPanel {
	private static final long serialVersionUID = 1L;
	private Juego Juego;
	private int tam = 10;
	private int probabilidad = 10;
	private JPanel LaminaBuscaMinas = new JPanel();
	private JPanel LaminaArriba = new JPanel();
	private JComboBox<String> opciones = new JComboBox<>(new String[] { "Principiante", "Intermedio", "Experto" });

	public LaminaPrincipal() {
		setLayout(new BorderLayout());

		LaminaArriba.add(generarTitulo());
		LaminaArriba.add(opciones);
		LaminaArriba.add(generarBotonComenzar());

		add(LaminaBuscaMinas, BorderLayout.CENTER);
		add(LaminaArriba, BorderLayout.NORTH);
		add(generarFooter(), BorderLayout.SOUTH);
	}

	/*
	 * Este método se encarga de comenzar/resetear el juego
	 * 
	 * Según la opción del combobox modifica el tamaño del tablero y el del layout
	 * del jpanel que muestra el buscaminas
	 * 
	 * E inicia la clase juego, que se encarga de toda la lógica del juego
	 * 
	 */
	public void comenzarJuego() {
		
		if (opciones.getSelectedItem() == "Principiante") {
			tam = 10;
			probabilidad = 10;
			//Se multiplica por tam, para tener siempre las casillas del mismo tamaño
			BuscaMinas.frame.setSize(40 * tam, 47 * tam);
			//Relativo a null, lo pone siempre centrado a la pantalla
			BuscaMinas.frame.setLocationRelativeTo(null);
			LaminaBuscaMinas.setLayout(new GridLayout(tam, tam));
		}
		if (opciones.getSelectedItem() == "Intermedio") {
			tam = 15;
			probabilidad = 20;
			BuscaMinas.frame.setSize(40 * tam, 47 * tam);
			BuscaMinas.frame.setLocationRelativeTo(null);
			LaminaBuscaMinas.setLayout(new GridLayout(tam, tam));
		}
		if (opciones.getSelectedItem() == "Experto") {
			tam = 20;
			probabilidad = 30;
			BuscaMinas.frame.setSize(40 * tam, 47 * tam);
			BuscaMinas.frame.setLocationRelativeTo(null);
			LaminaBuscaMinas.setLayout(new GridLayout(tam, tam));
		}
		// Resetear o iniciar tablero
		LaminaBuscaMinas.removeAll();
		casilla tablero[][] = new casilla[tam][tam];
		for (int x = 0; x <= tam - 1; x++) {
			for (int y = 0; y <= tam - 1; y++) {
				tablero[x][y] = new casilla(x, y);
				LaminaBuscaMinas.add(tablero[x][y]);
			}
		}
		// Reinicia la clase juego
		Juego = new Juego(tablero, tam , probabilidad);
		LaminaBuscaMinas.revalidate();
		LaminaBuscaMinas.repaint();
	}

	// Este metodo devuele un boton comenzar con un accion listener que llama a
	// comenzar juego
	private JButton generarBotonComenzar() {
		JButton comenzar = new JButton("Comenzar");
		comenzar.addActionListener(new ActionListener() {
			@Override

			public void actionPerformed(ActionEvent e) {
				comenzarJuego();
			}

		});
		return comenzar;
	}

	private static JLabel generarFooter() {
		JLabel JLFooter = new JLabel("Autor: 5yex.", SwingConstants.LEFT);
		JLFooter.setFont(new Font("arial", Font.ITALIC, 12));
		JLFooter.setBorder(new EmptyBorder(6, 8, 6, 0));
		JLFooter.setBackground(Color.BLACK);
		JLFooter.setOpaque(true);
		JLFooter.setForeground(Color.WHITE);
		return JLFooter;
	}

	private static JLabel generarTitulo() {
		JLabel JLtitulo = new JLabel("Bienvenido al Buscaminas");
		JLtitulo.setFont(new Font("arial", Font.BOLD, 12));
		return JLtitulo;
	}

	// Clase casilla que actua de JButton.
	class casilla extends JButton {
		private static final long serialVersionUID = -5243986073617792630L;
		private boolean TengoMina = false;
		private boolean TengoBandera = false;
		private int coordenadaX = 0;
		private int coordenadaY = 0;

		public casilla(int x, int y) {
			this.coordenadaX = x;
			this.coordenadaY = y;
			/*
			 * Añado el mouse listener en el que según si pulsas boton derecho o izquierdo
			 * llama a juego haciendo una acción diferente
			 * 
			 * Además comprueba si ha perdido o ganado la partida despues de realizar cada
			 * pulsación
			 * 
			 */

			this.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					// Comprubrueba que boton del ratón ha pulsado
					if (e.getButton() == MouseEvent.BUTTON1) {
						Juego.pulsacionPrincipal(coordenadaX, coordenadaY);
					}

					if (e.getButton() == MouseEvent.BUTTON3) {
						Juego.pulsacionSecundaria(coordenadaX, coordenadaY);
					}

					// Comprueba la variable que dice si la partida se ha perido
					if (Juego.isPartidaPerdida()) {
						JOptionPane.showMessageDialog(null, "Has pisado una mina, ¡Has perdido!");
						comenzarJuego();
					}
					// Llama al metodo que comprueba si hay ganador
					if (Juego.hayGanador()) {
						JOptionPane.showMessageDialog(null, "Enhorabuena, ¡Has ganado!");
						comenzarJuego();
					}
				}

				// Estos dos métodos ponen el borde negro cuando el ratón
				// esta encima de una casilla
				public void mouseEntered(MouseEvent e) {
					// borde negro
					((JButton) e.getSource()).setBorder(BorderFactory.createLineBorder(Color.black));
				}

				public void mouseExited(MouseEvent e) {
					// borde por defecto de un jbutton
					((JButton) e.getSource()).setBorder(new JButton().getBorder());
				}
			});
		}

		public int getCoordenadaX() {
			return coordenadaX;
		}

		public void setCoordenadaX(int coordenadaX) {
			this.coordenadaX = coordenadaX;
		}

		public int getCoordenadaY() {
			return coordenadaY;
		}

		public void setCoordenadaY(int coordenadaY) {
			this.coordenadaY = coordenadaY;
		}

		public boolean isTengoMina() {
			return TengoMina;
		}

		public void setTengoMina(boolean tengoMina) {
			TengoMina = tengoMina;
		}

		public boolean isTengoBandera() {
			return TengoBandera;
		}

		public void setTengoBandera(boolean tengoBandera) {
			TengoBandera = tengoBandera;
		}

	}

}
