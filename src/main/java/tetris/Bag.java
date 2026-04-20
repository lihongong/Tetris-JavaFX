package tetris;

import tetris.util.Copyable;

import java.util.ArrayList;
import java.util.Random;

/**
 * Keep a set of objects and refill the bag automatically (by cloning the objects) when it is empty.
 */
public class Bag<T extends Copyable<T>> {
    private final ArrayList<T> fixedSetOfItems = new ArrayList<>();
    private final ArrayList<T> bagOfItems = new ArrayList<>();

    /**
     * Initialise the bag and fill it with items.
     */
    public Bag(T... items) {
        for (int i = 0; i < items.length; i++) {
            fixedSetOfItems.add(items[i]);
            bagOfItems.add(items[i]);
        }
    }
    /**
     * Randomly picks a object from the bag until the bag is empty.
     * If the bag is empty, automatically refill the bag and pick again.
     */
    public T pickRandomly() {
        int numOfItemsLeft = this.bagOfItems.size();
        if (numOfItemsLeft > 0) {
            int random = new Random().nextInt(numOfItemsLeft);
            return this.bagOfItems.remove(random);
        } else {
            refill();
            numOfItemsLeft = this.bagOfItems.size();
            int random = new Random().nextInt(numOfItemsLeft);
            return this.bagOfItems.remove(random);
        }
    }
    private void refill() {
        for (T fixedSetOfItem : fixedSetOfItems) {
            bagOfItems.add(fixedSetOfItem.copy());
        }
    }

}
