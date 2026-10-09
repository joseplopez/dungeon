package com.game.dungeon.data.db

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MaterialDaoTest {

    private lateinit var database: GameDatabase
    private lateinit var dao: MaterialDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, GameDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.materialDao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testAddMaterialAddsQuantitiesAtomically() = runBlocking {
        // Initial state
        assertNull(dao.getMaterialById("iron_ore"))

        // Add 5
        dao.addMaterial("iron_ore", 5)
        val firstAdd = dao.getMaterialById("iron_ore")
        assertNotNull(firstAdd)
        assertEquals(5, firstAdd?.amount)

        // Add 10 atomically
        dao.addMaterial("iron_ore", 10)
        val secondAdd = dao.getMaterialById("iron_ore")
        assertEquals(15, secondAdd?.amount)

        // Adding 0 or negative should be ignored
        dao.addMaterial("iron_ore", 0)
        assertEquals(15, dao.getMaterialById("iron_ore")?.amount)

        dao.addMaterial("iron_ore", -5)
        assertEquals(15, dao.getMaterialById("iron_ore")?.amount)
    }

    @Test
    fun testDeductMaterialDecrementsAndPreventsNegativeBalance() = runBlocking {
        // Setup initial balance of 20
        dao.addMaterial("mithril_ore", 20)
        assertEquals(20, dao.getMaterialById("mithril_ore")?.amount)

        // Valid deduction
        val deducted5 = dao.deductMaterial("mithril_ore", 5)
        assertTrue(deducted5)
        assertEquals(15, dao.getMaterialById("mithril_ore")?.amount)

        // Attempt over-deduction (available 15, deduct 20)
        val overDeduct = dao.deductMaterial("mithril_ore", 20)
        assertFalse(overDeduct)
        assertEquals(15, dao.getMaterialById("mithril_ore")?.amount)

        // Deduct remaining 15
        val deducted15 = dao.deductMaterial("mithril_ore", 15)
        assertTrue(deducted15)
        assertEquals(0, dao.getMaterialById("mithril_ore")?.amount)

        // Attempt deduction on 0 balance
        val deductFromZero = dao.deductMaterial("mithril_ore", 1)
        assertFalse(deductFromZero)
        assertEquals(0, dao.getMaterialById("mithril_ore")?.amount)

        // Deduct non-existent material
        val deductNonExistent = dao.deductMaterial("non_existent", 5)
        assertFalse(deductNonExistent)

        // Deduct zero or negative amount returns true and does not alter balance
        val deductZero = dao.deductMaterial("mithril_ore", 0)
        assertTrue(deductZero)
        assertEquals(0, dao.getMaterialById("mithril_ore")?.amount)
    }

    @Test
    fun testGetMaterialsAndGetMaterialFlowEmissions() = runBlocking {
        // Initial Flow emissions
        val initialMaterials = dao.getMaterials().first()
        assertTrue(initialMaterials.isEmpty())

        val initialSingle = dao.getMaterial("fire_essence").first()
        assertNull(initialSingle)

        // Add materials
        dao.addMaterial("fire_essence", 8)
        dao.addMaterial("ruby_gem_1", 3)

        // Verify getMaterials() Flow
        val materialsList = dao.getMaterials().first()
        assertEquals(2, materialsList.size)
        assertTrue(materialsList.any { (it.id == "fire_essence") && (it.amount == 8) })
        assertTrue(materialsList.any { (it.id == "ruby_gem_1") && (it.amount == 3) })

        // Verify getMaterial(id) Flow
        val fireEssenceFlow = dao.getMaterial("fire_essence").first()
        assertNotNull(fireEssenceFlow)
        assertEquals("fire_essence", fireEssenceFlow?.id)
        assertEquals(8, fireEssenceFlow?.amount)

        // Update via deduction and check updated Flow emission
        dao.deductMaterial("fire_essence", 3)
        val updatedFireEssence = dao.getMaterial("fire_essence").first()
        assertEquals(5, updatedFireEssence?.amount)
    }
}
