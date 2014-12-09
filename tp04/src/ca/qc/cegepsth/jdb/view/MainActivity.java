package ca.qc.cegepsth.jdb.view;

import ca.qc.cegepsth.jdb.R;
import ca.qc.cegepsth.jdb.R.id;
import ca.qc.cegepsth.jdb.R.layout;
import ca.qc.cegepsth.jdb.R.menu;
import ca.qc.cegepsth.jdb.model.EvenementJournal;
import ca.qc.cegepsth.jdb.model.Journal;
import ca.qc.cegepsth.jdb.model.Punch;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static Journal jdb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.activity_main);
		if (jdb == null) {// Un peu le "First Launch"
			jdb = new Journal("Journal1", this); // On cr�e un nouveau Journal
			jdb.Path = getFilesDir(); // On lui donne la variable du Path o� on
										// enregistre les fichiers
		}

		// Loader les sessions de travail pr�c�dente
		jdb.JournaldeBord = jdb.loadFromDevice(this);
		// Afficher une Toast le nombre d'�v�nement dans le Journal
		Toast toast;
		Context context = this;
		String text;
		text = ("Nombre d'evenement au Journal : " + String
				.valueOf(MainActivity.jdb.size()));
		toast = Toast.makeText(context, text, 1);
		toast.setGravity(Gravity.TOP, 0, 15);
		toast.show();

	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         M�thode qui part l'Activit� TimeTrack
	 */
	public void mTimeTrack(View view) {
		Intent intent = new Intent(this, TimeTrackActivity.class);
		startActivity(intent);
	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         M�thode qui part l'activit� ReadLog
	 */
	public void mReadLog(View view) {
		Intent intent = new Intent(this, ReadLogActivity.class);

		startActivity(intent);
	}

	/**
	 * 
	 * @author Charles Perreault
	 * 
	 *         M�thode qui part l'activit� d'�criture dans le journal
	 */
	public void mWriteLog(View view) {
		// Determiner si on peut ecrire
		if (PunchedIn()) { // si on est PUNCH� ACTIF, ALORS ON PEUT TRAVAILLER
							// ET ECRIRE DANS LE JOURNAL
			Intent intent = new Intent(this, WriteLogActivity.class); // on part
																		// l'activit�
			startActivity(intent);
		} else {
			Toast toast;
			Context context = this;
			String text;
			text = ("Vous devez avoir punch� IN pour �crire dans le journal"); // qui
																				// est
																				// �crit
																				// �a
																				// dessus
			toast = Toast.makeText(context, text, 1);
			toast.setGravity(Gravity.TOP, 0, 15);
			toast.show();
		}
	}

	public void mCalendar(View view) {
		Intent intent = new Intent(this, CalendarActivity.class);

		startActivity(intent);
	}

	/**
	 * 
	 * @author Anthony Pugliese, Michael Carignan-Jacob
	 * 
	 *         V�rifie dans la liste si le dernier �l�ment est un punch IN
	 */
	private boolean PunchedIn() {
		if (!MainActivity.jdb.JournaldeBord.isEmpty()) {// Si
			EvenementJournal dernierEnevement = MainActivity.jdb
					.findLastEvent();
			
			if (dernierEnevement instanceof Punch) {
				Punch pi = (Punch) dernierEnevement;
				return pi.isPunchedIn;
			}
		}
		return false;
	}

	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @author Charles **/
	public void CleanJournal(View view)
	{
		boolean result = jdb.DestroyAndClean();
		
		if (result) { // donc �a a march�
			//On repart l'activit�
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		} else { // �a a plant� :(
			Toast toast;
			Context context = this;
			String text;
			text = ("Erreur lors de l'effacement du journal");
			toast = Toast.makeText(context, text, 1);
			toast.setGravity(Gravity.CENTER, 0, 15);
			toast.show();
		}
		
	}
}
