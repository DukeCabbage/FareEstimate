package dispatch.digital.fareestimator.searchAddress;


public interface SearchAddressContract {

    interface View {
        void showSearchResults(String[] results);
    }

    interface Presenter {
        void searchAddress(String input);

        void start();

        void stop();
    }
}
