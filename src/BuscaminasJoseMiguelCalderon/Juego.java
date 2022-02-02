package BuscaminasJoseMiguelCalderon;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import BuscaminasJoseMiguelCalderon.LaminaPrincipal.casilla;

public class Juego {
	private casilla[][] tablero;
	// Array de minas generadas usado para descubrirlas al ganar o perder, mas
	// eficientemente sin recorrer el tablero entero
	private ArrayList<casilla> minas = new ArrayList<casilla>();
	private int tamañoTablero = 0;
	private int probabilidadMinas = 0;
	private int casillasDescubiertas = 0;
	private int minasGeneradas = 0;
	private boolean partidaPerdida = false;
	private ImageIcon iconBandera = new ImageIcon(
			new ImageIcon("recursos/bandera.png").getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH));
	private ImageIcon iconMina = new ImageIcon(
			new ImageIcon("recursos/mina.png").getImage().getScaledInstance(25, 25, java.awt.Image.SCALE_SMOOTH));

	/*
	 * Se inicializa con el tablero y su tamaño
	 */
	public Juego(casilla[][] tablero, int tam, int probabilidadMinas) {
		this.tablero = tablero;
		this.tamañoTablero = tam;
		this.probabilidadMinas = probabilidadMinas;
	}

	// Acciones al pulsar con el click derecho
	public void pulsacionPrincipal(int cordX, int cordY) {
		casilla casillaActual = tablero[cordX][cordY];
		// si no hay minas generadas
		if (minasGeneradas == 0) {
			// Genera todas las minas del tablero
			generadorMinas(casillaActual);
			// Despeja la zona al rededor de la primera casilla pulsada
			// para tener una jugabilidad correcta evitando
			// perder nada mas empezar o que sea muy dificil el comienzo
			for (int x = cordX - 1; x <= cordX + 1; x++) {
				for (int y = cordY - 1; y <= cordY + 1; y++) {
					try {
						if (tablero[x][y].isTengoMina()) {
							minasGeneradas--;
							tablero[x][y].setTengoMina(false);
							minas.remove(tablero[x][y]);
						}
					} catch (ArrayIndexOutOfBoundsException e) {
					}
				}
			}
		}
		// Si no tiene banderia y esta habilitada
		if (!casillaActual.isTengoBandera() && casillaActual.isEnabled()) {
			// Si tiene mina termina la partida
			// Si no tiene mina llama al método para destapar casillas
			if (casillaActual.isTengoMina()) {
				terminarPartida();
			} else {
				DescubrirEspacio(cordX, cordY);
			}
		}
	}

	// Genera minas por todo el tablero según la probailidad por casilla
	public void generadorMinas(casilla casillaActual) {
		for (int x = 0; x <= tamañoTablero - 1; x++) {
			for (int y = 0; y <= tamañoTablero - 1; y++) {
				if (obtenerBooleanProbabilidad(probabilidadMinas) && tablero[x][y] != casillaActual) {
					minasGeneradas++;
					tablero[x][y].setTengoMina(true);
					// las casillas con mina se guardan tambíen en un array de minas
					minas.add(tablero[x][y]);
				}
			}
		}
	}

	// devuelve true o false, según la probabilidad por parametro
	public boolean obtenerBooleanProbabilidad(int probabilidad) {
		double valorRandom = Math.random() * 100; // probabilidad del 1 al 100
		return valorRandom <= probabilidad;
	}
	/*
	 * Método Recursivo para revelar espacios vacios
	 * 
	 * Comprueba si la casilla pulsada tiene número de adyacentes y si es 0, avanza
	 * recursivamente llamandose a si mismo con las casillas siguientes, hasta ir
	 * parando en las que tienen número de adyacentes
	 */

	public void DescubrirEspacio(int cordX, int cordY) {
		casilla casillaActual = tablero[cordX][cordY];
		// Si tiene bandera se para
		if (!casillaActual.isTengoBandera()) {
			casillaActual.setEnabled(false);
			casillasDescubiertas++;
			// Cuenta las minas que hay al rededor
			int minasEncontradas = contarMinasAyacentes(cordX, cordY);
			if (minasEncontradas > 0) {
				// si hay minas al rededor pone la icono correspondiente al numero
				icono2Casilla(casillaActual, number2Icon(minasEncontradas));
			} else {
				// Parte recursiva en la que avanza por las casillas de alrededor
				// hasta que se pare en casillas con númerp
				for (int x = cordX - 1; x <= cordX + 1; x++) {
					for (int y = cordY - 1; y <= cordY + 1; y++) {
						try {
							if (tablero[x][y].isEnabled()) {
								DescubrirEspacio(x, y);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
						}
					}
				}
			}
		}
	}

	/*
	 * Cuenta las minas adyacentes a la casilla dada y devuelve el número
	 */
	public int contarMinasAyacentes(int cordX, int cordY) {
		int minasDescubiertas = 0;
		for (int x = cordX - 1; x <= cordX + 1; x++) {
			for (int y = cordY - 1; y <= cordY + 1; y++) {
				try {
					casilla cTemp = tablero[x][y];
					if (cTemp.isTengoMina()) {
						minasDescubiertas++;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
				}
			}
		}
		return minasDescubiertas;
	}

	/*
	 * Método que comprueba si hay ganador
	 * 
	 * y descubre las minas encontradas
	 * 
	 * Da true siempre que Se descubran todas las casillas que no sean minas
	 */
	public boolean hayGanador() {
		if (minasGeneradas != 0 && (minasGeneradas + casillasDescubiertas) == (tamañoTablero * tamañoTablero)) {
			descubrirMinas(Color.green);
			return true;
		}
		return false;
	}

	// Metotodo para terminar la partida
	public void terminarPartida() {
		partidaPerdida = true;
		descubrirMinas(Color.red);
	}

	public boolean isPartidaPerdida() {
		return partidaPerdida;
	}

	// Recorre el array casillas con mina, las descubre y les pone el fondo de color
	public void descubrirMinas(Color color) {
		for (casilla casilla : minas) {
			casilla.setBackground(color);
			casilla.setEnabled(false);
			icono2Casilla(casilla, iconMina);
		}
	}

	// devuelve el icono con numero correspondiente al numero pasado por parámetro
	public ImageIcon number2Icon(int n) {
		return new ImageIcon(new ImageIcon("recursos/" + n + ".png").getImage().getScaledInstance(25, 25,
				java.awt.Image.SCALE_SMOOTH));
	}

	// Pone icono a una casilla
	public void icono2Casilla(casilla casilla, ImageIcon icon) {
		casilla.setIcon(icon);// Si no hacia esto antes me daba error
		casilla.setDisabledIcon(casilla.getIcon());
	}

	// Se activa cuando usas el botón izquierdo, pone y quita bandera
	public void pulsacionSecundaria(int coordenadaX, int coordenadaY) {
		casilla casillaActual = tablero[coordenadaX][coordenadaY];
		if (casillaActual.isEnabled()) {
			if (casillaActual.isTengoBandera() == false) {
				casillaActual.setIcon(iconBandera);
				casillaActual.setTengoBandera(true);

			} else {
				casillaActual.setIcon(null);
				casillaActual.setTengoBandera(false);
			}
		}
	}
}
