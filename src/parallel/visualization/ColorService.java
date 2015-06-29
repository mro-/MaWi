package parallel.visualization;

import javafx.scene.paint.Color;
import parallel.InitializeParameter;
import parallel.init.SharedVariables;

/**
 * Kapselt alle Methoden und Daten zur farblichen Gestaltung der anzuzeigenden
 * Fläche.
 */
public class ColorService {

	/**
	 * 101 Farben zur Darstellung der Farben von warm (rot) bis kalt (blau).
	 */
	private static final Color[] COLORS = { Color.rgb(28, 12, 93),
			Color.rgb(32, 20, 126), Color.rgb(32, 23, 130),
			Color.rgb(32, 25, 148), Color.rgb(31, 27, 156),
			Color.rgb(31, 30, 166), Color.rgb(31, 33, 179),
			Color.rgb(31, 34, 185), Color.rgb(32, 36, 191),
			Color.rgb(32, 37, 198), Color.rgb(31, 39, 204),
			Color.rgb(30, 40, 210), Color.rgb(31, 43, 224),
			Color.rgb(35, 47, 237), Color.rgb(34, 47, 240),
			Color.rgb(31, 50, 242), Color.rgb(32, 63, 239),
			Color.rgb(38, 76, 240), Color.rgb(46, 100, 240),
			Color.rgb(49, 152, 239), Color.rgb(57, 166, 240),
			Color.rgb(61, 176, 243), Color.rgb(62, 201, 226),
			Color.rgb(65, 215, 225), Color.rgb(65, 223, 227),
			Color.rgb(64, 225, 208), Color.rgb(65, 224, 193),
			Color.rgb(64, 225, 145), Color.rgb(63, 224, 104),
			Color.rgb(77, 226, 50), Color.rgb(83, 224, 37),
			Color.rgb(100, 230, 24), Color.rgb(109, 235, 22),
			Color.rgb(112, 231, 14), Color.rgb(128, 237, 13),
			Color.rgb(137, 239, 12), Color.rgb(155, 240, 13),
			Color.rgb(167, 241, 17), Color.rgb(172, 240, 17),
			Color.rgb(184, 242, 16), Color.rgb(191, 243, 15),
			Color.rgb(197, 245, 13), Color.rgb(205, 249, 17),
			Color.rgb(208, 248, 14), Color.rgb(214, 248, 14),
			Color.rgb(221, 244, 15), Color.rgb(226, 240, 16),
			Color.rgb(227, 238, 16), Color.rgb(225, 232, 17),
			Color.rgb(223, 228, 19), Color.rgb(224, 230, 19),
			Color.rgb(223, 226, 18), Color.rgb(224, 221, 16),
			Color.rgb(226, 217, 16), Color.rgb(225, 209, 15),
			Color.rgb(224, 201, 15), Color.rgb(224, 197, 16),
			Color.rgb(223, 193, 17), Color.rgb(225, 183, 17),
			Color.rgb(226, 177, 16), Color.rgb(225, 176, 15),
			Color.rgb(225, 176, 14), Color.rgb(224, 168, 13),
			Color.rgb(223, 162, 14), Color.rgb(224, 160, 14),
			Color.rgb(225, 158, 16), Color.rgb(227, 158, 18),
			Color.rgb(225, 150, 15), Color.rgb(223, 145, 13),
			Color.rgb(222, 140, 13), Color.rgb(221, 138, 13),
			Color.rgb(224, 135, 19), Color.rgb(212, 119, 12),
			Color.rgb(210, 114, 16), Color.rgb(210, 112, 17),
			Color.rgb(211, 110, 18), Color.rgb(210, 102, 15),
			Color.rgb(210, 98, 13), Color.rgb(210, 93, 13),
			Color.rgb(210, 89, 13), Color.rgb(208, 79, 18),
			Color.rgb(207, 76, 18), Color.rgb(206, 73, 18),
			Color.rgb(207, 66, 20), Color.rgb(208, 58, 27),
			Color.rgb(208, 51, 31), Color.rgb(211, 45, 35),
			Color.rgb(208, 39, 32), Color.rgb(208, 38, 33),
			Color.rgb(206, 36, 34), Color.rgb(200, 32, 31),
			Color.rgb(197, 32, 31), Color.rgb(190, 32, 31),
			Color.rgb(185, 32, 31), Color.rgb(180, 32, 31),
			Color.rgb(175, 32, 31), Color.rgb(170, 32, 31),
			Color.rgb(165, 32, 31), Color.rgb(160, 32, 31),
			Color.rgb(155, 32, 31), Color.rgb(150, 32, 31) };

	/**
	 * Berechnung des Farbwertes und schreiben in das Array, das die Farbwerte
	 * für die 2D Darstellung enthält. <br>
	 */
	public static void computeAndSetColor(float temperature, int x, int y) {
		// Temperatur auf 0-1 Skala mappen
		// (value-min)/(max-min)
		float mappedTemperatureF = (temperature - InitializeParameter.MIN_TEMP)
				/ (InitializeParameter.MAX_TEMP - InitializeParameter.MIN_TEMP);
		mappedTemperatureF = (mappedTemperatureF < 0) ? 0
				: ((mappedTemperatureF > 1) ? 1 : mappedTemperatureF);

		// Mappen auf 0-100 Skala
		int mappedTemperatureI = Math.round(mappedTemperatureF * 100);

		SharedVariables.tempInColor[x][y] = COLORS[mappedTemperatureI];
	}

}
