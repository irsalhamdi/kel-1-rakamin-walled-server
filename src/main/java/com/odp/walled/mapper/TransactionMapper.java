package com.odp.walled.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;
import com.odp.walled.dto.TransactionResponse;
import com.odp.walled.model.Transaction;

@Mapper(componentModel = "spring")
public interface TransactionMapper {
    @Mapping(source = "wallet.id", target = "walletId")
    @Mapping(source = "recipientWallet.id", target = "recipientWalletId")
    @Mapping(source = "wallet.accountNumber", target = "senderAccountNumber")
    @Mapping(source = "wallet.user.fullname", target = "senderFullname")
    @Mapping(source = "recipientWallet.accountNumber", target = "receiverAccountNumber")
    @Mapping(source = "recipientWallet.user.fullname", target = "receiverFullname")
    TransactionResponse toResponse(Transaction transaction);

    List<TransactionResponse> toResponseList(List<Transaction> transactions);
}