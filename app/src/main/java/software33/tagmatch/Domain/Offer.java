package software33.tagmatch.Domain;

public class Offer {

    private int offerId;
    private String userThatOffers;
    private String destinedUser;
    private int offerAdvertisement;
    private int offeredExchangeAdvertisement;
    private String offeredText;
    private boolean accepted;

    public Offer(){
        this.offerId = 0;
        this.userThatOffers = "";
        this.destinedUser = "";
        this.offerAdvertisement = 0;
        this.offeredExchangeAdvertisement = 0;
        this.offeredText = "";
        this.accepted = false;
    }

    public Offer(int offerId, String userThatOffers, String destinedUser, int offerAdvertisement, int offeredExchangeAdvertisement,
                 String offeredText, boolean accepted){
        this.offerId = offerId;
        this.userThatOffers = userThatOffers;
        this.destinedUser = destinedUser;
        this.offerAdvertisement = offerAdvertisement;
        this.offeredExchangeAdvertisement = offeredExchangeAdvertisement;
        this.offeredText = offeredText;
        this.accepted = accepted;
    }


    public int getOfferId() {
        return offerId;
    }

    public String getUserThatOffers() {
        return userThatOffers;
    }

    public String getDestinedUser() {
        return destinedUser;
    }

    public int getOfferAdvertisement() {
        return offerAdvertisement;
    }

    public int getOfferedExchangeAdvertisement() {
        return offeredExchangeAdvertisement;
    }

    public String getOfferedText() {
        return offeredText;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
