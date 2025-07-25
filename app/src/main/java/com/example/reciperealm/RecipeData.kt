package com.example.reciperealm

object RecipeData {
    val allRecipes = listOf(
        Recipe(
            id = "mie_ayam_01",
            title = "Mie Ayam",
            description = "Mie ayam adalah hidangan khas Indonesia yang terbuat dari mi gandum kuning...",
            imageResId = R.drawable.mie_ayam_placeholder,
            ingredients = "• 500 gr mie basah\n• 300 gr daging ayam, potong dadu\n• 3 sdm kecap manis\n• Sawi hijau secukupnya\n• Daun bawang, iris tipis",
            steps = "1. Tumis bumbu halus hingga harum.\n2. Masukkan potongan ayam, masak hingga matang.\n3. Rebus mie dan sawi.\n4. Sajikan mie dengan topping ayam dan taburan daun bawang."
        ),
        Recipe(
            id = "kupat_tahu_02",
            title = "Kupat Tahu",
            description = "Kupat tahu adalah makanan tradisional Indonesia yang berbahan dasar ketupat, tahu goreng...",
            imageResId = R.drawable.kupat_tahu_placeholder,
            ingredients = "• 5 buah ketupat\n• 10 buah tahu kuning, goreng\n• 250 gr tauge, seduh air panas\n• Bumbu kacang siap pakai\n• Bawang goreng dan kerupuk",
            steps = "1. Potong-potong ketupat dan tahu.\n2. Tambahkan tauge.\n3. Siram dengan bumbu kacang yang sudah diencerkan.\n4. Taburi bawang goreng dan sajikan dengan kerupuk."
        ),
        Recipe(
            id = "sate_ayam_03",
            title = "Sate Ayam",
            description = "Sate Ayam adalah makanan khas Indonesia yang terbuat dari potongan daging ayam...",
            imageResId = R.drawable.sate,
            ingredients = "• 500 gr fillet ayam, potong dadu\n• Tusuk sate secukupnya\n• Bumbu kacang\n• Kecap manis\n• Bawang merah dan cabai rawit iris",
            steps = "1. Tusuk daging ayam ke tusukan sate.\n2. Bakar sate sambil diolesi kecap hingga matang.\n3. Sajikan dengan bumbu kacang, irisan bawang merah, dan cabai."
        )
    )

    fun findRecipeById(id: String): Recipe? {
        return allRecipes.find { it.id == id }
    }
}