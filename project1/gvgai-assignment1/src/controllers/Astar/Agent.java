package controllers.Astar;
import java.util.ArrayList;
import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import tools.ElapsedCpuTimer;
import tools.Vector2d;
import java.util.Comparator;  
import java.util.PriorityQueue;  
import java.util.Queue; 

class Astar {
	public StateObservation state;
	public Types.ACTIONS action;
	public Astar father;
	public double fcost;
	public double hcost;
	public double gcost;
	public Vector2d rootPosition;
	ArrayList<Types.ACTIONS> resultAction = new ArrayList();
	public Astar() {
		father = null;
	}
	public Astar(ArrayList<Types.ACTIONS> resultAction) {
		this.resultAction = resultAction;
	}
	
	public double heuristics(StateObservation stateObs) {
		if (stateObs.getGameWinner() == Types.WINNER.PLAYER_WINS) return 0;
		if (stateObs.isGameOver()) return 1000000;
		ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
		ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
		Vector2d currentPosition = stateObs.getAvatarPosition();
		gcost =  currentPosition.dist(rootPosition);
		if (stateObs.getAvatarType() != 4) {
			Vector2d keypos = movingPositions[0].get(0).position;
			fcost = 100*currentPosition.dist(keypos);
		}
		else {
			Vector2d goalpos = fixedPositions[fixedPositions.length-1].get(0).position;
			fcost = currentPosition.dist(goalpos);
		}
		hcost = gcost + fcost;
		return hcost;
	}
	
	public Astar(StateObservation so,Types.ACTIONS action, Vector2d root) {
		rootPosition = root;
		this.action = action;
		state = so.copy();
		heuristics(so);
	}
	
	Comparator<Astar> Order = new Comparator<Astar> () {
		public int compare(Astar o1,Astar o2) {
			return (int)(o1.hcost - o2.hcost);
		}
	};
//	public Vector2d rootPosition;
	boolean finish;
	
	Queue<Astar> Open = new PriorityQueue<Astar>(Order);
	Queue<Astar> Closed = new PriorityQueue<Astar>(Order);
	public void search(StateObservation stateObs) {
		rootPosition = stateObs.getAvatarPosition();
		Astar firstAstar = new Astar(stateObs,null,rootPosition);
		Open.add(firstAstar);
		do {
			Astar priorityAstar = Open.poll();
			Closed.add(priorityAstar);
			ArrayList<Types.ACTIONS> newAction = stateObs.getAvailableActions();
			for (int i = 0;i < newAction.size();i++) {
				StateObservation stCopy = priorityAstar.state.copy();
				//System.out.println(newAction);
				stCopy.advance(newAction.get(i));
				Astar currentAstar = new Astar(stCopy,newAction.get(i),rootPosition);
				boolean flaga = false;
				boolean flagb = false;
				for (Astar s:Closed) {
					if (stCopy.equalPosition(s.state) == true)
						flaga = true;
				}
				for (Astar s:Open) {
					if (stCopy.equalPosition(s.state) == true)
						flagb = true;
				}
				if (stCopy.equalPosition(priorityAstar.state) == true || flaga == true) 
					continue;
				//System.out.println(newAction.get(i));
				if (flagb == false) {
					//System.out.println("11111");
					currentAstar.father = priorityAstar;
					Open.add(currentAstar);
					//System.out.println(Open.size());
				}
				if (flagb == true) {
					for (Astar s:Open) {
						if (stCopy.equalPosition(s.state) == true) {
							if (s.gcost > currentAstar.gcost) {
								s.father = priorityAstar;
								s.gcost = currentAstar.gcost;
								s.hcost = currentAstar.hcost;
								s.fcost = currentAstar.hcost;
							}
						}
					}
				}
				for (Astar s:Open) {
					if (s.state.getGameWinner() == Types.WINNER.PLAYER_WINS) {
						while (s != null) {
							resultAction.add(s.action);
							//System.out.println(resultAction);
							s = s.father;
						}
						finish = true;
					}
				}
			}
		} while (finish == false);
	}
}


public class Agent extends AbstractPlayer {
	int count;
	ArrayList<Types.ACTIONS> resultAction = new ArrayList();
	
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer) {
		count = 0;
	}
	
	public Types.ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		Astar s = new Astar(resultAction);
		if (count == 0) 
			s.search(stateObs);
		count++; 
		return resultAction.get(resultAction.size()-count);	
	}
}