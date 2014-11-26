package com.example.tp04;

import com.example.tp04putain.R;

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
        if(jdb == null){// Un peu le "First Launch"
        	jdb = new Journal("Journal1", this); // On crée un nouveau Journal
	        jdb.Path = getFilesDir(); // On lui donne la variable du Path où on enregistre les fichiers
        }
         
        // Loader les sessions de travail précédente
        jdb.JournaldeBord = jdb.loadFromDevice(this); 
        //Afficher une Toast le nombre d'événement dans le Journal 
        Toast toast;
		Context context = this;
		String text;
		text = ("Nombre d'evenement au Journal : " + String.valueOf(MainActivity.jdb.size()));
		toast = Toast.makeText(context, text, 1);
		toast.setGravity(Gravity.TOP, 0, 15);
		toast.show();
        
	}
	
	/**
	*
	* @author Charles Perreault
	*
	* Méthode qui part l'Activité TimeTrack
	*/
    public void mTimeTrack(View view) {
    	Intent intent = new Intent (this, TimeTrackActivity.class); 
    	startActivity(intent);
    }
    
	/**
	*
	* @author Charles Perreault
	*
	* Méthode qui part l'activité ReadLog
	*/
    public void mReadLog(View view) {
    	Intent intent = new Intent (this, ReadLogActivity.class); 
   
    	startActivity(intent);
    }

	/**
	*
	* @author Charles Perreault
	*
	* Méthode qui part l'activité d'écriture dans le journal
	*/
    public void mWriteLog(View view) {
    	//Determiner si on peut ecrire
    	if (PunchedIn()) { // si on est PUNCHÉ ACTIF, ALORS ON PEUT TRAVAILLER ET ECRIRE DANS LE JOURNAL
    		Intent intent = new Intent (this, WriteLogActivity.class); //on part l'activité
        	startActivity(intent);
		} else {
			Toast toast;
			Context context = this;
			String text;
			text = ("Vous devez avoir punché IN pour écrire dans le journal"); //qui est écrit ça dessus
			toast = Toast.makeText(context, text, 1);
			toast.setGravity(Gravity.TOP, 0, 15);
			toast.show();
		}
    }
    
    public void mCalendar(View view)
    {
    	Intent intent = new Intent (this, Calendar.class); 
    	
    	startActivity(intent);
    }
    
    public void Tasks(View view)
    {
    	Intent intent = new Intent (this, TasksActivity.class); 
    	
    	startActivity(intent);
    }
    
	/**
	*
	* @author Anthony Pugliese, Michael Carignan-Jacob
	*
	* Vérifie dans la liste si le dernier élément est un punch IN 
	*/
    private boolean PunchedIn(){
    	if(!MainActivity.jdb.JournaldeBord.isEmpty()){//Si
			evenementJournal dernierEnevement =	MainActivity.jdb.findLastEvent();
			if(dernierEnevement.type == 1){ //c'est un punch in
				return true;
			}else
				if(dernierEnevement.type == 3){ //Il vient d'écrire dans le journal, donc il est déjà punch IN.
					return true;
				}
    	}
			return false;
    }    
	
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
}
