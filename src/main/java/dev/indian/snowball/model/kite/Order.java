package dev.indian.snowball.model.kite;

import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class Order {
    public String exchangeOrderId;
    public String disclosedQuantity;
    public String validity;
    public String tradingSymbol;
    public String orderVariety;
    public String orderType;
    public String triggerPrice;
    public String statusMessage;
    public String price;
    public String status;
    public String product;
    public String accountId;
    public String exchange;
    public String orderId;
    public String pendingQuantity;
    public Date orderTimestamp;
    public Date exchangeTimestamp;
    public Date exchangeUpdateTimestamp;
    public String averagePrice;
    public String transactionType;
    public String filledQuantity;
    public String quantity;
    public String parentOrderId;
    public String tag;
    public String guid;
    public int validityTTL;
    public Map<String, Object> meta;
    public String auctionNumber;

    public static Order toOrder(com.zerodhatech.models.Order order) {
        Order kiteOrder = new Order();
        kiteOrder.setExchangeOrderId(order.exchangeOrderId);
        kiteOrder.setDisclosedQuantity(order.disclosedQuantity);
        kiteOrder.setOrderTimestamp(order.orderTimestamp);
        kiteOrder.setExchangeTimestamp(order.exchangeTimestamp);
        kiteOrder.setExchangeUpdateTimestamp(order.exchangeUpdateTimestamp);
        kiteOrder.setOrderVariety(order.orderVariety);
        kiteOrder.setTriggerPrice(order.triggerPrice);
        kiteOrder.setStatusMessage(order.statusMessage);
        kiteOrder.setPrice(order.price);
        kiteOrder.setOrderType(order.orderType);
        kiteOrder.setAccountId(order.accountId);
        kiteOrder.setParentOrderId(order.parentOrderId);
        kiteOrder.setGuid(order.guid);
        kiteOrder.setValidityTTL(order.validityTTL);
        kiteOrder.setMeta(order.meta);
        kiteOrder.setAuctionNumber(order.auctionNumber);
        kiteOrder.setOrderId(order.orderId);
        kiteOrder.setExchange(order.exchange);
        kiteOrder.setTradingSymbol(order.tradingSymbol);
        kiteOrder.setTransactionType(order.transactionType);
        kiteOrder.setQuantity(order.quantity);
        kiteOrder.setProduct(order.product);
        kiteOrder.setStatus(order.status);
        kiteOrder.setValidity(order.validity);
        kiteOrder.setAveragePrice(order.averagePrice);
        kiteOrder.setFilledQuantity(order.filledQuantity);
        kiteOrder.setPendingQuantity(order.pendingQuantity);
        kiteOrder.setTag(order.tag);

        return kiteOrder;
    }
}
