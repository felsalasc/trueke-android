package com.example.trueke

import com.example.trueke.model.Product
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductOwnershipTest {

    private val product = Product(owner = "Publicador", ownerUid = "owner-uid")

    @Test
    fun ownerCanManageProduct() {
        assertTrue(product.isOwnedBy("owner-uid"))
    }

    @Test
    fun differentUserCannotManageProduct() {
        assertFalse(product.isOwnedBy("other-uid"))
    }

    @Test
    fun signedOutUserCannotManageProduct() {
        assertFalse(product.isOwnedBy(null))
    }

    @Test
    fun blankUidCannotManageProduct() {
        assertFalse(product.isOwnedBy(""))
        assertFalse(product.isOwnedBy("   "))
    }

    @Test
    fun legacyProductCannotBeClaimedUsingDisplayOwner() {
        val legacyProduct = Product(owner = "owner@example.com")
        assertFalse(legacyProduct.isOwnedBy("owner@example.com"))
        assertFalse(legacyProduct.isOwnedBy("owner-uid"))
        assertFalse(legacyProduct.isOwnedBy(""))
    }

    @Test
    fun matchingDisplayOwnerDoesNotGrantOwnership() {
        assertFalse(product.copy(owner = "other-uid").isOwnedBy("other-uid"))
    }
}
