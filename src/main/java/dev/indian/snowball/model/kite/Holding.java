package dev.indian.snowball.model.kite;

import lombok.Data;

import java.util.Date;

@Data
public class Holding {
    public String product;
    public Double lastPrice;
    public String price;
    public String tradingSymbol;
    public int t1Quantity;
    public String collateralQuantity;
    public String collateralType;
    public String isin;
    public Double pnl;
    public int quantity;
    public String realisedQuantity;
    public Double averagePrice;
    public String exchange;
    public String instrumentToken;
    public int usedQuantity;
    public int authorisedQuantity;
    public Date authorisedDate;
    public boolean discrepancy;
    public double dayChange;
    public double dayChangePercentage;

    public static Holding toHolding(com.zerodhatech.models.Holding holding) {
        Holding kiteHolding = new Holding();
        kiteHolding.setProduct(holding.product);
        kiteHolding.setLastPrice(holding.lastPrice);
        kiteHolding.setPrice(holding.price);
        kiteHolding.setTradingSymbol(holding.tradingSymbol);
        kiteHolding.setT1Quantity(holding.t1Quantity);
        kiteHolding.setCollateralQuantity(holding.collateralQuantity);
        kiteHolding.setCollateralType(holding.collateraltype);
        kiteHolding.setIsin(holding.isin);
        kiteHolding.setPnl(holding.pnl);
        kiteHolding.setQuantity(holding.quantity);
        kiteHolding.setRealisedQuantity(holding.realisedQuantity);
        kiteHolding.setAveragePrice(holding.averagePrice);
        kiteHolding.setExchange(holding.exchange);
        kiteHolding.setInstrumentToken(holding.instrumentToken);
        kiteHolding.setUsedQuantity(holding.usedQuantity);
        kiteHolding.setAuthorisedQuantity(holding.authorisedQuantity);
        kiteHolding.setAuthorisedDate(holding.authorisedDate);
        kiteHolding.setDiscrepancy(holding.discrepancy);
        kiteHolding.setDayChange(holding.dayChange);
        kiteHolding.setDayChangePercentage(holding.dayChangePercentage);

        return kiteHolding;
    }
}