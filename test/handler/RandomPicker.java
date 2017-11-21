package handler;

import java.util.BitSet;

import org.junit.Test;

public class RandomPicker {

	@Test
	public void pickRandomBitSetField() {
		MessageHandler msghndlr = new MessageHandler(0, 0, null);
		BitSet b = new BitSet(10);
		b.set(2);
		b.set(3);
		b.set(8);
		b.set(9);

		System.out.println(msghndlr.pickRandomIndex(b));
	}

}
