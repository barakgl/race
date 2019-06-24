package game.arenas;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.swing.JOptionPane;

import game.arenas.exceptions.RacerLimitException;
import game.arenas.exceptions.RacerTypeException;
import game.racers.Racer;
import gui.PaintJPanel;
import gui.raceGui;
import utilities.EnumContainer.RacerEvent;
import utilities.Point;

public abstract class Arena implements Observer {

	private final static int MIN_Y_GAP = 10;
	private ArrayList<Racer> activeRacers;
	private ArrayList<Racer> compleatedRacers;
	private ArrayList<Racer> brokenRacers;
	private ArrayList<Racer> disabledRacers;
	private double length;
	private final int MAX_RACERS;
	private final double FRICTION;
	private  static volatile boolean tmp = false;

	/**
	 * 
	 * @param length
	 *            the x value for the finish line
	 * @param maxRacers
	 *            Maximum number of racers
	 * @param friction
	 *            Coefficient of friction
	 * 
	 */
	protected Arena(double length, int maxRacers, double friction) {
		this.length = length;
		this.MAX_RACERS = maxRacers;
		this.FRICTION = friction;
		this.activeRacers = new ArrayList<Racer>();
		this.compleatedRacers = new ArrayList<Racer>();
	}

	public void update(Observable observable, Object arg)
	{
		RacerEvent event = (RacerEvent) arg;
		switch (event)
		{
		case BROKENDOWN:
			this.brokenRacers.add((Racer)observable);
			this.activeRacers.remove((Racer)observable);
			break;
		case DISABLED:
			this.disabledRacers.add((Racer)observable);
			this.activeRacers.remove((Racer)observable);
			break;
		case FINISHED:
			this.compleatedRacers.add((Racer)observable);
			this.activeRacers.remove((Racer)observable);
			System.out.println("racer finished");
			//JOptionPane.showMessageDialog(null, "race finished");
			tmp = this.hasActiveRacers();
			if (tmp==false)
			{
				raceGui.setInRace(false);
				raceGui.setRaceEnded(true);
				//JOptionPane.showMessageDialog(null, "race finished");
				System.out.println("race finished");
			}
			break;
		case REPAIRED:
			break;
		default:
			break;
		
		}
	}
	
	public void startRace()
	{
		
		ExecutorService pool = Executors.newFixedThreadPool(this.MAX_RACERS);
		for (Racer r: this.activeRacers)
		{
			r.setFinish(new Point(this.length,0));
			r.setCurrentLocation(new Point(0,0));
			pool.execute(r);
		}
		pool.shutdown();
		while(!pool.isTerminated()) { }
		
	}
	
	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public static int getMinYGap() {
		return MIN_Y_GAP;
	}

	public int getMAX_RACERS() {
		return MAX_RACERS;
	}

	public double getFRICTION() {
		return FRICTION;
	}

	public void setActiveRacers(ArrayList<Racer> activeRacers) {
		this.activeRacers = activeRacers;
	}

	public void setCompleatedRacers(ArrayList<Racer> compleatedRacers) {
		this.compleatedRacers = compleatedRacers;
	}

	public void addRacer(Racer newRacer) throws RacerLimitException, RacerTypeException {
		if (this.activeRacers.size() == this.MAX_RACERS) {
			throw new RacerLimitException(this.MAX_RACERS, newRacer.getSerialNumber());
		}
		this.activeRacers.add(newRacer);
		
	}
	
	
	
	/*
	public void crossFinishLine(Racer racer) {
		this.compleatedRacers.add(racer);
	}
	 */
	
	public ArrayList<Racer> getActiveRacers() {
		return activeRacers;
	}

	public ArrayList<Racer> getCompleatedRacers() {
		return compleatedRacers;
	}

	public boolean hasActiveRacers() {
		return this.activeRacers.size() > 0;
	}

	public void initRace() {
		int y = 0;
		for (Racer racer : this.activeRacers) {
			Point s = new Point(0, y);
			Point f = new Point(this.length, y);
			racer.initRace(this, s, f);
			y += Arena.MIN_Y_GAP;
		}
	}

	/*
	public void playTurn() {
		for (Racer racer : this.activeRacers) {
			racer.move(this.FRICTION);
		}

		for (Racer r : this.compleatedRacers)
			this.activeRacers.remove(r);
	}
*/
	
	public void showResults() {
		for (Racer r : this.compleatedRacers) {
			String s = "#" + this.compleatedRacers.indexOf(r) + " -> ";
			s += r.describeRacer();
			System.out.println(s);
		}
		// for (int i = 0; i < this.activeRacers.size(); i++) {
		// System.out.println("#" + (i + 1) + ": " + this.activeRacers.get(i));
		// }
	}
	
	public ArrayList<Racer> getBrokenRacers() {
		return brokenRacers;
	}

	public void setBrokenRacers(ArrayList<Racer> brokenRacers) {
		this.brokenRacers = brokenRacers;
	}

	public ArrayList<Racer> getDisabledRacers() {
		return disabledRacers;
	}

	public void setDisabledRacers(ArrayList<Racer> disabledRacers) {
		this.disabledRacers = disabledRacers;
	}

}
