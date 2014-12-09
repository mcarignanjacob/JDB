package ca.qc.cegepsth.jdb.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract.Events;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Journal {
	public String Name;
	public File Path;
	public List<EvenementJournal> JournaldeBord;
	public CalendarHandler ch;
	Context context;

	// Constructeur
	public Journal(String name, Context context) {
		this.context = context;
		Name = name;
		JournaldeBord = new ArrayList<EvenementJournal>();
		CalendarHandler ch = new CalendarHandler();
	}

	/**
	 * @author Charles
	 */
	// Methode qui ajoute un evenement en background
	public void addEventHIDEMODE(Calendar bTime, Calendar eTime, String Title) {
		ArrayList<Calendar> Alcal = SetTimeOrderCorrectly(bTime, eTime);

		bTime = Alcal.get(0);
		eTime = Alcal.get(1);

		final ContentValues event = new ContentValues();
		event.put(Events.CALENDAR_ID, 1);

		Calendar beginTime = Calendar.getInstance();
		beginTime = bTime; // le d�but = au debut
		Calendar endTime = Calendar.getInstance();
		endTime = eTime; // la fin = � la fin

		event.put(Events.TITLE, Title);
		event.put(Events.DESCRIPTION, "Description");
		event.put(Events.EVENT_LOCATION, "Just here");

		event.put(Events.DTSTART, beginTime.getTimeInMillis());
		event.put(Events.DTEND, endTime.getTimeInMillis());
		event.put(Events.ALL_DAY, 0); // 0 for false, 1 for true
		event.put(Events.HAS_ALARM, 1); // 0 for false, 1 for true

		String timeZone = TimeZone.getDefault().getID();
		event.put(Events.EVENT_TIMEZONE, timeZone);

		Uri baseUri;
		if (Build.VERSION.SDK_INT >= 8) {
			baseUri = Uri.parse("content://com.android.calendar/events");
		} else {
			baseUri = Uri.parse("content://calendar/events");
		}

		context.getContentResolver().insert(baseUri, event);
	}

	private ArrayList<Calendar> SetTimeOrderCorrectly(Calendar bTime,
			Calendar eTime) {
		ArrayList<Calendar> ALcal = new ArrayList<Calendar>();

		Calendar BeginTime = bTime;
		Calendar EndTime = eTime;
		if (EndTime.getTimeInMillis() > BeginTime.getTimeInMillis()) {
			// Si on se rend ici, le temps de fin est avant le d�but et on doit
			// les Swapper
			Calendar Temp;
			Temp = BeginTime;
			BeginTime = EndTime;
			EndTime = Temp;
		}
		// Maintenant on retourne l'ArrayList de Calendar Dans l'ordre
		// -> L'ORDRE EST TRES IMPORTANT ICI
		ALcal.add(BeginTime);
		ALcal.add(EndTime);

		return ALcal;

	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         Retourne le nombre d'items dans la liste
	 */
	public int size() {
		int t;
		t = JournaldeBord.size();
		return t;
	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         Retourne vrai si existe
	 */
	public boolean exist() {
		boolean answer = false;
		if (size() > 0)
			answer = true;
		return answer;

	}

	/**
	 * 
	 * @author Charles Perreault, Anthony Pugliese
	 * 
	 *         Retourne le dernier �v�nement dans la liste selon le type entr�
	 *         en param�tre
	 */
	public EvenementJournal findLastEvent() {
		EvenementJournal ejTemp = null;
		int dernierElement = JournaldeBord.size() - 1;
		ejTemp = JournaldeBord.get(dernierElement);
		return ejTemp;
	}

	/**
	 * 
	 * @author Charles Perreault, Anthony Pugliese
	 * 
	 *         S�rialise la liste d'�v�nement en format Json
	 */
	private String serialize(List<EvenementJournal> listJDB) {

		Gson gson = new Gson();
		GsonBuilder builder = new GsonBuilder();
		String JournaldeBordEnJSON = "";
		// / Initialisation du s�rialiseur JSON

		JournaldeBordEnJSON = new GsonBuilder().create().toJson(listJDB);
		builder.setPrettyPrinting();
		gson = builder.create();
		JournaldeBordEnJSON = gson.toJson(listJDB);

		// S�rialisation

		return JournaldeBordEnJSON;
	}

	/**
	 * 
	 * @author Charles Perreault , Anthony Pugliese
	 * 
	 *         Sauvegarde sur l'appareil
	 */
	public boolean saveToDevice(Context context) {
		// On r�cup�re ce qu'il faut �crire...
		String aSauvegarder = serialize(JournaldeBord);

		String nomDuFichier = "JDBenJSON"; // Le nom du fichier
		// File pathFichier = Path; // L'endroit o� on l'�crit

		// Moteur d'�criture
		FileOutputStream outputStream;
		try {
			outputStream = context.openFileOutput(nomDuFichier,
					Context.MODE_PRIVATE);
			outputStream.write(aSauvegarder.getBytes()); // on �crit le contenu
															// de la string
			outputStream.close(); // fermeture du Fichier
		} catch (IOException e) {
			// Normalement on ne devrait pas arriver ici, SI c'est le cas on
			// affichera un messageBox explicant le probl�me
			AlertDialog.Builder messBoxAlert = new AlertDialog.Builder(context);// cr�e
																				// un
																				// alert
																				// messageBox
			messBoxAlert.setTitle("Save to Device - probleme");
			messBoxAlert.setMessage("Le fichier n'a pas ete trouver ("
					+ nomDuFichier + ")");
			messBoxAlert.setCancelable(true);
			messBoxAlert.show();
			return false;
		}

		return true; // On retourne Vrai si il n'y a aucun probl�me et que on se
						// rend jusqu'ici
	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         Retourne une liste d'evenements Journals
	 */
	public List<EvenementJournal> loadFromDevice(Context ctx) {
		String _Name = "JDBenJSON"; // le m�me nom de fichier
		File _Path = Path; // le m�me path

		// une liste vide
		ArrayList<EvenementJournal> JournaldeBord = new ArrayList<EvenementJournal>();

		try {
			File serialisedObject = new File(_Path, _Name);

			if (!serialisedObject.exists()) {

			} else {
				FileReader fr = new FileReader(
						serialisedObject.getCanonicalFile());
				BufferedReader bufferedReader = new BufferedReader(fr);
				// Lecture du fichier
				String line;
				StringBuilder sb = new StringBuilder();
				while ((line = bufferedReader.readLine()) != null)
					sb.append(line);
				bufferedReader.close();
				String contenuDuFichier = sb.toString();
				// D�finition de la structure du fichier JSON
				Type typeJournal = new TypeToken<ArrayList<EvenementJournal>>() {
				}.getType();
				// Cr�ation des instances a� partir du fichier, groupe en une
				// collection
				Gson gson = new Gson();
				JournaldeBord = gson.fromJson(contenuDuFichier, typeJournal);
			}
		} catch (IOException e) {
			// Normalement on ne devrait pas arriver ici, SI c'est le cas on
			// affichera un messageBox explicant le probl�me
			AlertDialog.Builder messBoxAlert = new AlertDialog.Builder(ctx);// cr�e
																			// un
																			// alert
																			// messageBox
			messBoxAlert.setTitle("loadFromDevice - Probleme");
			messBoxAlert.setMessage("Le fichier n'a pas ete trouver (" + _Name
					+ ")");
			messBoxAlert.setCancelable(true);
			messBoxAlert.show();
			return JournaldeBord;
		}
		return JournaldeBord;
	}

	public boolean DestroyAndClean()
	{
		try {
			//On enleve le fichier de backup
		   if (Path.isDirectory()) {
				String[] children = Path.list();
				
				for (int i = 0; i < children.length; i++) {
					new File(Path, children[i]).delete();
				}
			}
		} catch (Exception e) {
			return false;
		}
		//On enleve les events de la collection un � un.. 
		//Pas tr�s optimis� mais fait la job en fin de session.
		try {
			for (int i = 0; i < JournaldeBord.size(); i++) {
				JournaldeBord.remove(JournaldeBord.get(i));
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}
