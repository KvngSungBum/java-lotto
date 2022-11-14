package lotto;

import camp.nextstep.edu.missionutils.Console;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Application {
    static HashMap<WinningPlace, Integer> winningHistory = new HashMap<>();
    static int inputPrice = 0;

    public static void main(String[] args) {
        System.out.println("구입금액을 입력해 주세요.");
        try {
            List<Lotto> myLottoTickets = buyLotto();
            printMyLottoTickets(myLottoTickets);

            Lotto winnerNumber = new Lotto(inputWinnerNumber());
            System.out.println(winnerNumber.getNumbers());
            System.out.println();

            int bonusNumber = inputBonusNumber();

            saveWinningPlaceByTicket(winnerNumber, myLottoTickets, bonusNumber);

            printLottoTicketHistory();

            printWinningsOutOfBuyingPrice(inputPrice);
        } catch (Exception e) {
        }

    }

    public static int checkUserInputCondition(String input) {
        for (int i = 0; i < input.length(); i++) {
            if (!Character.isDigit(input.charAt(i))) {
                throw new IllegalArgumentException(ErrorResponse.INPUT_CONTAINS_CHAR_ERROR.getErrorMessage());
            }
        }

        return Integer.parseInt(input);
    }

    public static int convertBuyingPriceIntoTicketAmount(int buyingPrice) {
        if (buyingPrice % 1000 != 0) {
            throw new IllegalArgumentException(ErrorResponse.INPUT_BUYING_RANGE_ERROR.getErrorMessage());
        }

        return buyingPrice / 1000;
    }

    public static List<Lotto> getLottoTickets(int ticketAmount) {
        List<Lotto> lottoTickets = new ArrayList<>();

        for (int i = 0; i < ticketAmount; i++) {
            List<Integer> randomLottoNumber = Lotto.createRandomLottoNumber();
            Lotto ticket = new Lotto(randomLottoNumber);
            lottoTickets.add(ticket);
        }

        return lottoTickets;
    }

    private static List<Lotto> buyLotto() {
        String userInput = Console.readLine();
        System.out.println();
        int buyingPrice, ticketAmount;

        try {
            buyingPrice = checkUserInputCondition(userInput);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_CONTAINS_CHAR_ERROR.getErrorMessage());
            throw new IllegalArgumentException();
        }

        inputPrice = buyingPrice;

        try {
            ticketAmount = convertBuyingPriceIntoTicketAmount(buyingPrice);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_BUYING_RANGE_ERROR.getErrorMessage());
            throw new IllegalArgumentException();
        }

        List<Lotto> lottoTickets = getLottoTickets(ticketAmount);

        return lottoTickets;
    }

    private static void printMyLottoTickets(List<Lotto> lottoTickets) {
        int size = lottoTickets.size();
        System.out.println(size + "개를 구매했습니다.");
        for (Lotto lottoTicket : lottoTickets) {
            System.out.println(lottoTicket.getNumbers());
        }
        System.out.println();
    }

    public static String validateWinnerNumberContainsComma(String userInput) {
        if (!userInput.contains(",")) {
            throw new IllegalArgumentException(ErrorResponse.INPUT_WITHOUT_COMMA_ERROR.getErrorMessage());
        }

        return userInput;
    }

    public static String validateWinnerNumberSize(String userInput) {
        String[] trimmedInput = userInput.split(",");

        if (trimmedInput.length != 6) {
            throw new IllegalArgumentException(ErrorResponse.INPUT_WRONG_SIZE_ERROR.getErrorMessage());
        }

        return userInput;
    }

    public static List<Integer> convertStringWinnerNumberIntoListWinnerNumber(String userInput) {
        List<Integer> convertedNumber = new ArrayList<>();

        String[] trimmedInput = userInput.split(",");
        for (String inputNumber : trimmedInput) {
            convertedNumber.add(Integer.parseInt(inputNumber));
        }

        return convertedNumber;
    }

    public static void validateWinnerNumberRange(List<Integer> winnerNumber) {
        for (Integer eachNumber : winnerNumber) {
            if (eachNumber < 1 || eachNumber > 45) {
                throw new IllegalArgumentException(ErrorResponse.INPUT_LOTTO_RANGE_ERROR.getErrorMessage());
            }
        }
    }

    private static List<Integer> inputWinnerNumber() {
        System.out.println("당첨 번호를 입력해 주세요.");
        String userInput = Console.readLine();
        String commaValidated, sizeValidated;

        try {
            commaValidated = validateWinnerNumberContainsComma(userInput);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_WITHOUT_COMMA_ERROR.getErrorMessage());
            throw new IllegalArgumentException();
        }

        try {
            sizeValidated = validateWinnerNumberSize(commaValidated);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_WITHOUT_COMMA_ERROR.getErrorMessage());
            throw new IllegalArgumentException();
        }

        List<Integer> convertedWinnerNumber = convertStringWinnerNumberIntoListWinnerNumber(sizeValidated);

        try {
            validateWinnerNumberRange(convertedWinnerNumber);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_LOTTO_RANGE_ERROR);
            throw new IllegalArgumentException();
        }

        return convertedWinnerNumber;
    }

    public static int validateBonusNumberRange(int bonusNumber) {
        if (bonusNumber < 1 || bonusNumber > 45) {
            throw new IllegalArgumentException(ErrorResponse.INPUT_BONUS_NUMBER_RANGE_ERROR.getErrorMessage());
        }

        return bonusNumber;
    }

    private static int inputBonusNumber() {
        System.out.println("보너스 번호를 입력해 주세요.");
        String userInput = Console.readLine();
        int bonusNumber;

        try {
            bonusNumber = checkUserInputCondition(userInput);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_LOTTO_RANGE_ERROR.getErrorMessage());
            throw new IllegalArgumentException();
        }

        try {
            bonusNumber = validateBonusNumberRange(bonusNumber);
        } catch (IllegalArgumentException e) {
            System.out.println(ErrorResponse.INPUT_BONUS_NUMBER_RANGE_ERROR);
            throw new IllegalArgumentException();
        }

        return bonusNumber;
    }

    private static void initWinningHistory() {
        for (WinningPlace value : WinningPlace.values()) {
            winningHistory.put(value, 0);
        }
    }

    public static WinningPlace countCorrespondingNumbers(Lotto winnerNumber, Lotto checkTargetNumber, int bonusNumber) {
        int correspondingNumber = 0;
        int correspondingBonusNumber = 0;

        for (Integer targetNumber : checkTargetNumber.getNumbers()) {
            if (winnerNumber.getNumbers().contains(targetNumber)) {
                correspondingNumber++;
            }
        }

        if (checkTargetNumber.getNumbers().contains(bonusNumber)) {
            correspondingBonusNumber++;
        }

        return WinningPlace.getPlace(correspondingNumber, correspondingBonusNumber);
    }

    public static void saveWinningPlaceByTicket(Lotto winnerNumber, List<Lotto> lottoTickets, int bonusNumber) {
        initWinningHistory();
        for (Lotto lottoTicket : lottoTickets) {
            WinningPlace winningPlace = countCorrespondingNumbers(winnerNumber, lottoTicket, bonusNumber);
            addToHistory(winningPlace);
        }
    }

    private static void addToHistory(WinningPlace winningPlace) {
        Integer prevHistory = winningHistory.get(winningPlace);
        winningHistory.put(winningPlace, prevHistory + 1);
    }

    private static void printLottoTicketHistory() {
        System.out.println("당첨 통계");
        System.out.println("---");
        System.out.println(WinningPlace.FIFTH_PLACE.getMessage() + winningHistory.get(WinningPlace.FIFTH_PLACE) + "개");
        System.out.println(WinningPlace.FOURTH_PLACE.getMessage() + winningHistory.get(WinningPlace.FOURTH_PLACE) + "개");
        System.out.println(WinningPlace.THIRD_PLACE.getMessage() + winningHistory.get(WinningPlace.THIRD_PLACE) + "개");
        System.out.println(WinningPlace.SECOND_PLACE.getMessage() + winningHistory.get(WinningPlace.SECOND_PLACE) + "개");
        System.out.println(WinningPlace.FIRST_PLACE.getMessage() + winningHistory.get(WinningPlace.FIRST_PLACE) + "개");
    }

    private static void printWinningsOutOfBuyingPrice(int buyingPrice) {
        int sumOfWinnings = getTotalWinnings();

        Double winningsRate = (Double.valueOf(sumOfWinnings) / Double.valueOf(buyingPrice)) * 100.0f;

        String trimmedWinningsRate = String.format("%.1f", winningsRate);

        System.out.println("총 수익률은 " + trimmedWinningsRate + "%입니다.");
    }

    private static int getTotalWinnings() {
        int totalWinnings = 0;
        for (WinningPlace winningPlace : winningHistory.keySet()) {
            if (!winningPlace.equals(WinningPlace.NOT_IN_PLACE) && winningHistory.get(winningPlace) != 0) {
                totalWinnings += winningPlace.getWinnings();
            }
        }

        return totalWinnings;
    }
}
