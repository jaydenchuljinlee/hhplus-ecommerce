package com.hhplus.ecommerce.user.api

import com.hhplus.ecommerce.common.dto.CustomApiResponse
import com.hhplus.ecommerce.user.api.dto.UserAddressRequest
import com.hhplus.ecommerce.user.api.dto.UserAddressResponse
import com.hhplus.ecommerce.user.domain.UserAddressService
import jakarta.validation.constraints.Min
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
class UserAddressController(
    private val userAddressService: UserAddressService
) : IUserAddressController {

    override fun addAddress(request: UserAddressRequest): ResponseEntity<CustomApiResponse<UserAddressResponse>> {
        val result = userAddressService.addAddress(
            userId = request.userId,
            alias = request.alias,
            receiverName = request.receiverName,
            phone = request.phone,
            address = request.address,
            detailAddress = request.detailAddress,
            zipCode = request.zipCode,
            isDefault = request.isDefault
        )
        return ResponseEntity.status(201).body(CustomApiResponse.created(UserAddressResponse.from(result)))
    }

    override fun updateAddress(
        @Min(1) addressId: Long,
        request: UserAddressRequest
    ): ResponseEntity<CustomApiResponse<UserAddressResponse>> {
        val result = userAddressService.updateAddress(
            addressId = addressId,
            userId = request.userId,
            alias = request.alias,
            receiverName = request.receiverName,
            phone = request.phone,
            address = request.address,
            detailAddress = request.detailAddress,
            zipCode = request.zipCode
        )
        return ResponseEntity.ok(CustomApiResponse.success(result).let {
            CustomApiResponse.success(UserAddressResponse.from(result))
        })
    }

    override fun deleteAddress(@Min(1) addressId: Long, @Min(1) userId: Long): ResponseEntity<Void> {
        userAddressService.deleteAddress(addressId, userId)
        return ResponseEntity.noContent().build()
    }

    override fun getAddresses(@Min(1) userId: Long): ResponseEntity<CustomApiResponse<List<UserAddressResponse>>> {
        val results = userAddressService.getAddresses(userId).map { UserAddressResponse.from(it) }
        return ResponseEntity.ok(CustomApiResponse.success(results))
    }
}
