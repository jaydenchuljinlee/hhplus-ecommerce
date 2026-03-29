package com.hhplus.ecommerce.user.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.user.api.dto.UserAddressRequest
import com.hhplus.ecommerce.user.api.dto.UserAddressResponse
import com.hhplus.ecommerce.user.domain.UserAddressService
import org.springframework.web.bind.annotation.RestController

@RestController
class UserAddressController(
    private val userAddressService: UserAddressService
) : IUserAddressController {

    override fun add(request: UserAddressRequest): CustomApiResponse<UserAddressResponse> {
        val result = userAddressService.add(
            userId = request.userId,
            alias = request.alias,
            receiverName = request.receiverName,
            phone = request.phone,
            address = request.address,
            zipCode = request.zipCode,
            isDefault = request.isDefault
        )
        return CustomApiResponse.success(UserAddressResponse.from(result))
    }

    override fun update(addressId: Long, request: UserAddressRequest): CustomApiResponse<UserAddressResponse> {
        val result = userAddressService.update(
            addressId = addressId,
            alias = request.alias,
            receiverName = request.receiverName,
            phone = request.phone,
            address = request.address,
            zipCode = request.zipCode,
            isDefault = request.isDefault
        )
        return CustomApiResponse.success(UserAddressResponse.from(result))
    }

    override fun delete(addressId: Long): CustomApiResponse<Unit> {
        userAddressService.delete(addressId)
        return CustomApiResponse.success()
    }

    override fun getList(userId: Long): CustomApiResponse<List<UserAddressResponse>> {
        val results = userAddressService.getList(userId)
        return CustomApiResponse.success(results.map { UserAddressResponse.from(it) })
    }
}
