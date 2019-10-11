package controllers.limitdepthfirst;

import java.util.ArrayList;

import core.game.Observation;
import core.game.StateObservation;
import core.player.AbstractPlayer;
import ontology.Types;
import ontology.Types.ACTIONS;
import tools.ElapsedCpuTimer;
import tools.Vector2d;

public class Agent extends AbstractPlayer{
	protected ArrayList<StateObservation> state = new ArrayList();
	protected ArrayList<Types.ACTIONS> act = new ArrayList();
	protected ArrayList<Types.ACTIONS> updateAction = new ArrayList();
	public int count;
	public boolean finish;
	public int depth;
	public double cost;
	public Agent(StateObservation so, ElapsedCpuTimer elapsedTimer)
    {
        count = 0;
        depth = 0;
        cost = 10000;
        finish = false;
        
    }
	
	public double distance(StateObservation stateObs) {
		double distance = 0;
		ArrayList<Observation>[] fixedPositions = stateObs.getImmovablePositions();
		ArrayList<Observation>[] movingPositions = stateObs.getMovablePositions();
		Vector2d goalpos = fixedPositions[1].get(0).position; //目标的坐标
		//Vector2d keypos = movingPositions[0].get(0).position;//钥匙的坐标
		Vector2d currentPosition = stateObs.getAvatarPosition();
//		System.out.println(movingPositions.length);
//		System.out.println(currentPosition);
		if (movingPositions.length == 2) {//如果还没吃到钥匙
			Vector2d keypos = movingPositions[0].get(0).position;
//			System.out.println(keypos);
			distance = currentPosition.dist(goalpos) + currentPosition.dist(keypos);
		//	System.out.println(distance);
		}	
		else {//如果吃到钥匙
//			Vector2d keypos1 = movingPositions[0].get(0).position;
//			System.out.println(keypos1);
			distance = currentPosition.dist(goalpos);
//			System.out.println(distance);
		}
		return distance;
	}
	
	public void dfs(StateObservation stateObs, int depth) {
		ArrayList<Types.ACTIONS> newAction = stateObs.getAvailableActions();
		state.add(stateObs);
		state.get(state.size()-1).stepCount = 0;
		depth++;
		for (int i = 0;i < newAction.size();i++) {
			boolean flag = true;
	//		if (finish) break;
			StateObservation stCopy = stateObs.copy();
			stCopy.advance(newAction.get(i));
			
			for (int j = 0;j < state.size();j++) {
				if (stCopy.equalPosition(state.get(j)) == true) {
					flag = false;
				}
			}
	/*		if (stCopy.getGameWinner() == Types.WINNER.PLAYER_LOSES) {
				flag = false;
			}*/
			if (stCopy.getGameWinner() == Types.WINNER.PLAYER_WINS) {
				//System.out.println(flag);
				finish = true;
				act.add(newAction.get(i));
				updateAction.clear();
				for (int a = 0;a < act.size();a++) {
					updateAction.add(act.get(a));
				}
			}
			if (finish) break;
			if (depth == 6 && flag == true) {
//				System.out.println("ok");
				if (distance(stCopy) < cost) {
					cost = distance(stCopy);
					//System.out.println("222222");
					updateAction.clear();
					for (int k = 0;k < act.size();k++) 
						updateAction.add(act.get(k));
				}
				flag = false;
			}
			if (flag) {
				act.add(newAction.get(i));
				dfs(stCopy, depth);
			}
			else {
				state.get(state.size()-1).stepCount++;
			}
			if (state.get(state.size()-1).stepCount == newAction.size()) {
				if (state.size() != 0 && act.size() != 0) {//添加判断条件，否则会报异常
					act.remove(act.size()-1);
					state.remove(state.size()-1);
					state.get(state.size()-1).stepCount++;
				}
			}
		}
	}

	@Override
	public ACTIONS act(StateObservation stateObs, ElapsedCpuTimer elapsedTimer) {
		// TODO Auto-generated method stub
		cost = 10000;
		if (finish == false) {
			act.clear();
			state.clear();
			updateAction.clear();
			dfs(stateObs, 0);
//			System.out.println(act.size());
//			System.out.println(updateAction.size());
//			System.out.println(updateAction.get(0));
//			return act.get(0);
			return updateAction.get(0);
		}
		else {
			count++;
	//		System.out.println(count);
			return act.get(count-1);
		}
//		return null;
	}
}
