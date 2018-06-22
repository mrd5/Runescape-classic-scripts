public final class MD_wildyHobgoblinLimps extends Script {
	
	private static final int LIMP = 220;
	private static final int LOBS = 373;
	private static final int DOORS_CLOSED = 64; //closed door id


	private int banked_count;

	private long start_time;

	public MD_wildyHobgoblinLimps(Extension ex){
		super(ex);
	}

	@Override
	public void init(String params){
		start_time = -1L;
		banked_count = 0;
	}

	@Override
	public int main(){
		if (start_time == -1L){//Gets current time
			start_time = System.currentTimeMillis();
		}

		if (getHpPercent() < 50 && getInventoryCount(LOBS) > 0){
			useItem(LOBS);
		}

		if (isQuestMenu()){//If talking to a banker, open the bank interface
			answer(0);
			return random(1000, 2000);
		}

		if (isBanking()){
			int count = getInventoryCount(LIMP);
			if (count > 0){
				banked_count += count;
				deposit(LIMP, count);
			}

			int foodCount = getInventoryCount(LOBS);
			if (foodCount < 5){
				withdraw(LOBS, 5 - foodCount);
			}

			closeBank();
			return random(1000, 2000);
		}

		if (getInventoryCount(LOBS) == 5 && !isAtApproxCoords(224,253,10)){
			walkTo(220, 258);
			return random(1000, 1500);
		}

		if (getFatigue() > 95 && inCombat()){
			walkTo(getPlayerX(0) + 1, getPlayerY(0));
			useSleepingBag();
			return random(1000, 1500);
		}
		else if (getFatigue() > 95){//Sleep if fatigue is almost at 100
			useSleepingBag();
			return random(1000, 1500);
		}

		if (isAtApproxCoords(224, 253, 10) && getInventoryCount(LOBS) > 0 && !inCombat()){
			int[] hobgoblins = getNpcById(67);
			attackNpc(hobgoblins[0]);
			return random(1000, 2000);
		}

		pickupItem(LIMP, getPlayerX(0), getPlayerY(0));

		if (isAtApproxCoords(224, 253, 10) && getInventoryCount(LOBS) == 0){
			walkTo(215, 450);
			return random(1000, 2000);
		}

		int[] banker = getNpcByIdNotTalk(BANKERS);//gets nearest banker which is free to talk to and talks to it
		if (banker[0] != -1){//if banker exists
			if (distanceTo(banker[1], banker[2]) > 2){
				if (checkDoors()){
					return random(1000, 2000);
				}
			}
			talkToNpc(banker[0]);
			return random(600, 800);
		}

		return 100;

	}

	private boolean checkDoors(){//Checks if doors are closed
		int[] closed = getObjectById(DOORS_CLOSED);
		if (closed[0] == -1){
			return false;
		}
		atObject(closed[1], closed[2]);
		return true;
	}
}