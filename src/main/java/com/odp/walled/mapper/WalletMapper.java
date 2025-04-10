package com.odp.walled.mapper;

import com.odp.walled.dto.UserResponse;
import com.odp.walled.dto.WalletResponse;
import com.odp.walled.model.User;
import com.odp.walled.model.Wallet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    @Mapping(source = "user.id", target = "userId")
    WalletResponse toResponse(Wallet wallet);

    List<WalletResponse> toResponseList(List<Wallet> wallets);

    UserResponse toUserResponse(User user);
}
