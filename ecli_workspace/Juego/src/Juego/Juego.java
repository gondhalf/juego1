package Juego;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;

import javax.swing.JFrame;

//creamo la clase juego le decimos que es un Canvas y le implementamos una interfaz Runnable 
//para poder usar diferentes threads para agilizar los procesos
public class Juego extends Canvas implements Runnable {

	// añade una id para futuros cambios (se puede hacer al pulsar sobre la
	// clase publica Juego)
	private static final long serialVersionUID = 1L;

	// constantes para decir el alto y ancho de nuestra ventana
	private static final int ANCHO = 800;
	private static final int ALTO = 600;

	// booleano para comprobar el correcto funcionamiento del juego (le decimos
	// que es volatile para que solo lo pueda usar 1 thread a la vez)
	private static volatile boolean enFuncionamiento = false;

	// nombre para la ventana
	private static final String NOMBRE = "Juego";

	// variable para actualizaciones por segundo
	private static int aps = 0;
	// variable para frames por segundo
	private static int fps = 0;

	// para añadir la ventana (se necesita importar el Jframe)
	private static JFrame ventana;

	// crear segundo thread
	private static Thread thread;

	private Juego() {

		// tamaño que tendra nuestra ventana
		setPreferredSize(new Dimension(ANCHO, ALTO));

		// iniciar objeto ventana
		ventana = new JFrame(NOMBRE);
		// establecer que al pulsar el boton cerrar se ciera la ventana
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// el usuario no pueda cambiar el tamaño de la ventana
		ventana.setResizable(false);
		// diseño de la ventana o organizacion interna, abra ke importarlo
		ventana.setLayout(new BorderLayout());
		// añadir a la ventana nuestra clase Juego es decir el Canvas y lo
		// centramos en mitad de la pantalla
		ventana.add(this, BorderLayout.CENTER);
		// para ajustar el contenido de la ventana al tamaño ke keremos
		ventana.pack();
		// fijar la ventaña en mitad del escritorio
		ventana.setLocationRelativeTo(null);
		// para ke la ventana sea visible
		ventana.setVisible(true);
	}

	// metodo main para poder ejecutar el programa
	public static void main(String[] args) {
		Juego juego = new Juego();
		// ejecuta el metodo "iniciar"
		juego.iniciar();
	}

	// metodo para iniciar el segundo thread (le decimos synchronized para que
	// se asegure que los metodos inicar y detener no puedan cambiar las
	// variables de forma simultanea)
	private synchronized void iniciar() {
		// cambia el valor a true para comprobar que se esta ejecutando
		enFuncionamiento = true;

		// iniciamos el thread y lo identificamos como Graficos
		thread = new Thread(this, "Graficos");
		// poner en ejecucion el thread
		thread.start();

	}

	// metodo para detener el segundo thread (synchronized ver metodo inicar)
	private synchronized void detener() {
		// cambia a false para comprobar que se detuvo
		enFuncionamiento = false;

		// intenta detener el thread de forma segura
		try {
			// detiene el thread (usando join() nos aseguramos que el thread
			// acabe de hacer lo que estaba haciendo antes de cerrarlo)
			thread.join();
		} catch (InterruptedException e) {
			// nos muestra por consola el error si no consigue detener
			e.printStackTrace();
		}

	}

	// metodo para actualizar las variables del juego: vida de jugador, objetos,
	// etc..
	private void actualizar() {
		// cada vez ke se ejecute este metodo se le añadira 1 mas a los APS
		aps++;
	}

	// metodo para redibujar los graficos del juego
	private void mostrar() {
		// cada vez ke se ejecute este metodo se le añadira 1 mas a los FPS
		fps++;

	}

	// es lo que ejecutara el segundo thread (ver class Juego (implements
	// Runnable))
	public void run() {
		// comprueba que se esta ejecutando el segundo thread
		/* System.out.print("El thread 2 se esta ejecutando"); */

		// Mide el tiempo en nanosegundos tomando como referencia los ciclos de
		// reloj del procesador (1s=1Millonanoseg)
		// System.nanoTime();

		// Cuantos nanosegundos hay en un segundo
		final int NS_POR_SEGUNDO = 1000000000;
		// Cuantas actualizaciones queremos llevar por segundo
		final byte APS_OBJETIVO = 60;
		// Cuantos nanosegundos ocurren por actualizacion
		final double NS_POR_ACTUALIZACION = NS_POR_SEGUNDO / APS_OBJETIVO;
		// Referencia de tiempo en nanosegundos en ese momento exacto
		long referenciaActualizacion = System.nanoTime();
		// Referencia de tiempo en nanosegundos en ese momento exacto
		long referenciaContador = System.nanoTime();

		// variable para asignar el tiempo transcurrido
		double tiempoTranscurrido;
		// variable para expresar la cantidad de tiempo que pasa hasta que se
		// realiza una actualizacion
		double delta = 0;

		// mientras enFuncioamiento sea verdadero se ejecutara lo siguiente
		while (enFuncionamiento) {

			// tomamos una referencia del tiempo actual en nanosegundos
			final long inicioBucle = System.nanoTime();
			// calculamos el tiempo transcurrido entre antes y despues de
			// empezar el bucle
			tiempoTranscurrido = inicioBucle - referenciaActualizacion;
			// le volvemos a asignar una nueva referencia de actualizacion para
			// poder volver a medir el tiempo transcurrido en ejecucion dentro
			// del bucle
			referenciaActualizacion = inicioBucle;
			// asignamos a delta el tiempo por actualizacion transcurrido
			delta += tiempoTranscurrido / NS_POR_ACTUALIZACION;
			// para ke actualizar se ejecute 60 veces cada segundo
			while (delta >= 1) {
				// cambiara 60 veces por segundo el estado del juego
				actualizar();
				// restamos para que el bucle se genere en su determinado
				// momento
				delta--;
			}

			mostrar();

			// se toma el tiempo de esta instante y se le resta la referecia de
			// contador y si la diferencia es mas de 1s se realizara una
			// actualizacion de contador
			if (System.nanoTime() - referenciaContador > NS_POR_SEGUNDO) {
				// muestra en el titulo los FPS y APS por cada cambio en el
				// tiempo
				ventana.setTitle(NOMBRE + " || APS: " + aps + " || FPS: " + fps);
				// se reinician las variables a 0 para que funcione y las vuelva
				// a recalcular desde 0
				aps = 0;
				fps = 0;
				// vuelve a tomar la referencia actual de tiempo
				referenciaContador = System.nanoTime();
			}

		}
	}
}
