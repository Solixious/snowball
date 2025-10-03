package dev.indian.snowball.model.kite;

import lombok.Data;

@Data
public class Margin {
    public Available available;
    public Utilised utilised;
    public String net;

    @Data
    public static class Available {
        public String cash;
        public String intradayPayin;
        public String adhocMargin;
        public String collateral;
        public String liveBalance;
    }

    @Data
    public static class Utilised {
        public String m2mUnrealised;
        public String m2mRealised;
        public String debits;
        public String span;
        public String optionPremium;
        public String holdingSales;
        public String exposure;
        public String turnover;
    }

    public static Margin toMargin(com.zerodhatech.models.Margin margin) {
        Margin kiteMargin = new Margin();
        kiteMargin.setAvailable(toAvailable(margin.available));
        kiteMargin.setUtilised(toUtilised(margin.utilised));
        kiteMargin.setNet(margin.net);

        return kiteMargin;
    }

    private static Margin.Available toAvailable(com.zerodhatech.models.Margin.Available available) {
        Margin.Available kiteAvailable = new Margin.Available();
        kiteAvailable.setCash(available.cash);
        kiteAvailable.setIntradayPayin(available.intradayPayin);
        kiteAvailable.setAdhocMargin(available.adhocMargin);
        kiteAvailable.setCollateral(available.collateral);
        kiteAvailable.setLiveBalance(available.liveBalance);

        return kiteAvailable;
    }

    private static Margin.Utilised toUtilised(com.zerodhatech.models.Margin.Utilised utilised) {
        Margin.Utilised kiteUtilised = new Margin.Utilised();
        kiteUtilised.setM2mUnrealised(utilised.m2mUnrealised);
        kiteUtilised.setM2mRealised(utilised.m2mRealised);
        kiteUtilised.setDebits(utilised.debits);
        kiteUtilised.setSpan(utilised.span);
        kiteUtilised.setOptionPremium(utilised.optionPremium);
        kiteUtilised.setHoldingSales(utilised.holdingSales);
        kiteUtilised.setExposure(utilised.exposure);
        kiteUtilised.setTurnover(utilised.turnover);

        return kiteUtilised;
    }
}
