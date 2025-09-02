import java.util.Arrays;
import java.util.List;

public class Day07 implements Day {

	static char[] cards;

    @Override
    public long part1(List<String> lines) {
		// padded with - to make symbol and index the same
		cards = new char[]{'-', '-','2', '3', '4', '5', '6', '7', '8', '9', 'T', 'J', 'Q', 'K', 'A'};

        Hand[] hands = lines.stream().parallel()
			.map(s -> s.split(" "))
			.map(s -> inithand(s, false))
			.toArray(Hand[]::new);

        Arrays.sort(hands, Day07::compare);

        int sum = 0;
        for (int i = 0; i < hands.length; i++) {
            final int factor = i+1;
            final int bid = hands[i].bid;
            sum += factor * bid;
        }

        return sum;
    }

    @Override
    public long part2(List<String> lines) {
		// padded with - to make symbol and index the same
        cards = new char[]{'-', 'J', '2', '3', '4', '5', '6', '7', '8', '9', 'T', 'Q', 'K', 'A'};

        Hand[] hands = lines.stream().parallel()
			.map(s -> s.split(" "))
			.map(s -> inithand(s, true))
			.toArray(Hand[]::new);

        Arrays.sort(hands, Day07::compare);

		// for (Hand hand : hands) System.out.println(hand);

        int sum = 0;
        for (int i = 0; i < hands.length; i++) {
            final int factor = i+1;
            final int bid = hands[i].bid;
            sum += factor * bid;
        }

        return sum;
    }

	private static int symbolToCard(char symbol){
		for (int i = 1; i < cards.length; i++) if(cards[i] == symbol) return i;
		throw new RuntimeException("Bad symbol: " + symbol);
	}

	private static Hand inithand(String[] s, boolean withJoker){
		int[] hand = s[0].chars().parallel()
			.mapToObj(i -> (char) i)
			.mapToInt(Day07::symbolToCard)
			.toArray();
		int bid = Integer.parseInt(s[1]);
		Strength st = Strength.evaluate(hand, withJoker);
		return new Hand(hand, bid, st);
	}

	private static int compare(Hand a, Hand b){
		int res = a.strength.ordinal() - b.strength.ordinal();

		if (res == 0)
			for (int i = 0; i < a.hand.length; i++) {
				int ac = a.hand[i];
				int bc = b.hand[i];
				res = ac - bc;
				if (res != 0) break;
			}

		return res;
	}

	private record Hand(int[] hand, int bid, Strength strength){
		public String toString(){
			StringBuilder res = new StringBuilder();
			for (int i : hand) res.append(" ").append(cards[i]);
			return res.toString();
		}
	}

	private enum Strength {
		HighCard, OnePair, TwoPair, ThreeKind, FullHouse, FourKind, FiveKind;

		static Strength evaluate(int[] hand, boolean withJoker) {
			final int joker = symbolToCard('J');
			int[] count = new int[cards.length];
			for (int c : hand) { // joker counts as any card
				if(withJoker && c==joker) for (int i = 0; i < cards.length; i++) count[i]++;
				else count[c]++;
			}

			int[] strengths = new int[Strength.values().length];

			for (int i : count) {
				if (i == 0) continue;
				Strength s = switch (i) {
					case 1 -> HighCard;
					case 2 -> OnePair;
					case 3 -> ThreeKind;
					case 4 -> FourKind;
					case 5 -> FiveKind;
					default -> throw new RuntimeException("How did we get here?!");
				};
				strengths[s.ordinal()]++;
			}

			if (strengths[FiveKind.ordinal()]  >= 1) 	return FiveKind;
			if (strengths[FourKind.ordinal()]  >= 1) 	return FourKind;
			if (strengths[ThreeKind.ordinal()] >= 1 &&
				strengths[OnePair.ordinal()]   >= 1) 	return FullHouse;
			if (strengths[ThreeKind.ordinal()] >= 1) 	return ThreeKind;
			if (strengths[OnePair.ordinal()]   >= 2) 	return TwoPair;
			if (strengths[OnePair.ordinal()]   == 1) 	return OnePair;

			return HighCard;
		}
	}
}
