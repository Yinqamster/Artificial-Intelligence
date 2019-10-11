package controllers.depthfirst;

import java.util.ArrayList;

import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;

public class Agent  extends AbstractPlayer {
//	public static Types.ACTIONS[] actions;
	protected ArrayList<StateObservation> state = new ArrayList();
	protected ArrayList<Types.ACTIONS> act = new ArrayList();
	public int count;
	public boolean finish;
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        count = 0;
        finish = false;
        
    }
	
	public void dfs(StateObservation stateObs) {
		//System.out.println(action);
		ArrayList<Types.ACTIONS> newAction = stateObs.getAvailableActions();
		state.add(stateObs);
		state.get(state.size()-1).stepCount = 0;
		for (int i = 0;i < newAction.size();i++) {
			boolean flag = true;
			if (finish) break;
			StateObservation stCopy = stateObs.copy();
			stCopy.advance(newAction.get(i));
			for (int j = 0;j < state.size();j++) {
				if (stCopy.equalPosition(state.get(j)) == true) {
					flag = false;
				}
			}
			if (stCopy.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
				flag = false;
			}
			if (stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
				//System.out.println(flag);
				finish = true;
			}
			if (flag) {
				act.add(newAction.get(i));
				dfs(stCopy);
			}
			else {
				state.get(state.size()-1).stepCount++;
			}
			if (state.get(state.size()-1).stepCount == newAction.size()) {
				act.remove(act.size()-1);
				state.remove(state.size()-1);
				state.get(state.size()-1).stepCount++;
			}
		}
	}
	

/*	public static void main(String[] args) {
		// TODO Auto-generated method stub
	}*/

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// TODO Auto-generated method stub
		if (count == 0) {
			dfs(stateObs);
		}
		count++;
	//	System.out.println(count);
		return act.get(count-1);
//		return null;
	}

}