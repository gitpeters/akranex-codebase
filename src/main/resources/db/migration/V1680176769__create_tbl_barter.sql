create table if not exists barter_offer
(
    id                          bigint primary key AUTO_INCREMENT,
    amount_to_be_paid           double NOT NULL,
    amount_to_be_received       double NOT NULL,
    rate                        double NOT NULL,
    transaction_fee             double NOT NULL,
    trading_currency            varchar(20) NOT NULL,
    receiving_currency          varchar(20) NOT NULL,
    offer_status                varchar(70) NULL,
    akranex_tag                 varchar(70) NOT NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

);

create table if not exists barter_bid
(
    id                          bigint NOT NULL primary key AUTO_INCREMENT,
    offer_id                    bigint NOT NULL,
    amount_to_be_paid           double NOT NULL,
    amount_to_be_received       double NOT NULL,
    rate                        double NOT NULL,
    trading_currency            varchar(30) NULL,
    receiving_currency          varchar(30) NULL,
    offer_amount                double NOT NULL,
    offer_rate                  double NOT NULL,
    akranex_tag                 varchar(50) NULL,
    bid_status                  varchar(50) NULL,
    created_on                  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_modified_on            TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);