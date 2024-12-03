package com.upi.masakin.data.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "recipes_table")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val ingredients: List<String>,
    val steps: List<String>,
    val description: String,
    val time: String,
    val serving: String,
    val reviews: String,
    val image: Int,
    val isFavorite: Boolean = false,
    val chefId: Int,
    val videoId: String,
    val rating: Float
) : Parcelable

const val DAUN_BAWANG = "• 1 batang daun bawang"
const val SAJIKAN = "Sajikan."

object RecipeData {
    fun getIngredientsList(): List<List<String>> {
        return listOf(
            listOf(
                "• 4 siung bawang putih, cincang",
                "• 150 gr udang cincang",
                "• 10 buah jamur kuping, iris tipis",
                "• 150 gr wortel, iris tipis",
                "• 150 gr kubis, iris tipis",
                "• 50 ml air",
                "• ½ sdt lada putih",
                "• 1 sachet Masako® Kaldu Jamur",
                "• 12 lembar kulit lumpia",
                "• 750 ml minyak untuk menggoreng"
            ), listOf(
                "• 2 siung bawang putih",
                DAUN_BAWANG,
                "• 3 buah tomat sedang",
                "• 1 buah tahu putih",
                "• 1 butir telur",
                "• 800 ml air",
                "• 1 sdt AJI-NO-MOTO®",
                "• 1½ sdt garam"
            ), listOf(
                "• 5 buah cakwe",
                "• 250 gr ayam cincang",
                "• 150 gr udang",
                "• 2 butir putih telur",
                "• 1 sdm kecap asin 1 sdm minyak wijen",
                "• 1 sdt garam",
                "• 1 sdm gula pasir",
                "• 2 sdt lada putih",
                "• 1 sdt AJI-NO-MOTO®",
                "• 3 sdm tepung sagu",
                "• 1½ sdm bawang goreng",
                "• 3 batang daun bawang",
                "• Minyak secukupnya untuk menggoreng",
                "• Mayumi® Pedas"
            ), listOf(
                "• 5 siung bawang merah",
                "• 3 siung bawang putih",
                "• 5 buah cabai rawit merah",
                "• 2 buah cabai merah keriting",
                "• 3 sdm minyak goreng",
                "• 1 bungkus AJINOMOTO® Terasi Udang",
                "• ½ sdt garam",
                "• ½ sdt Masako® Ayam",
                "• 2 butir telur",
                "• 300 gr nasi putih",
                "• 2 buah sosis sapi",
                "• Minyak untuk menumis"
            ), listOf(
                "• 250 gr dada ayam",
                "• 12 siung bawang merah",
                "• 12 buah cabai rawit merah",
                "• 3 batang serai",
                "• 8 lembar daun jeruk",
                "• 1 sdt AJI-NO-MOTO® Terasi Udang",
                "• ½ sdt gula",
                "• 8 sdm minyak kelapa",
                "• 2 sdm air perasan jeruk nipis",
                "• 1 sdt garam",
                "• ½ sdt AJI-NO-MOTO®"
            ), listOf(
                "• 300 gr Daging slice",
                "• 6 siung Bawang merah",
                "• 3 siung Bawang putih",
                "• 1 sdt Ketumbar",
                "• ½ sdm Kecap inggris",
                "• 1 ruas jari Lengkuas",
                "• ½ sdm Gula merah",
                "• ½ sdt Baking powder",
                "• ½ sdt garam",
                "• ½ sdt lada putih",
                "• 2 butir Telur",
                "• 1 Bungkus Sajiku® Tepung Bumbu Serbaguna Pedas"
            ), listOf(
                "• 1 buah sawi putih",
                "• 250 gr daging ayam cincang",
                "• 2 siung bawang putih, cincang",
                DAUN_BAWANG,
                "• 50 gr wortel, potong dadu",
                "• 1 gr jahe, parut",
                "• 1 sdm minyak wijen",
                "• 1½ sdt Masako® Light",
                "• Chilli oil",
            ), listOf(
                "• 250 gr daging slice",
                "• 250 gr Mie Hokkien",
                "• ½ bawang bombay, iris",
                "• 2 siung bawang putih",
                DAUN_BAWANG,
                "• 1 sdt SAORI® Bulgogi",
                "• 1 sdt Wijen Sangrai",
                "• 30 gr wortel, iris"
            ), listOf(
                "• 50 g daging ayam giling",
                "• 50 g jagung manis pipil",
                "• 200 g kentang rebus, haluskan",
                "• 3 sdm Sajiku® Ayam Goreng",
                "• 1 butir telur, kocok lepas",
                "• 3 sdm maizena",
                "• 1 butir telur, kocok lepas, untuk pelapis",
                "• 150 g tepung panir (orange)",
                "• Minyak untuk menggoreng"
            ), listOf(
                "• 1 L kaldu ayam",
                "• 4 sdm SAORI® Saus Teriyaki",
                "• 5 sdm gula merah, sisir",
                "• 6 buah tahu putih kecil, potong 2 persegi",
                "• 1 buah kentang, potong cantik",
                "• 1 buah wortel, potong cantik",
                "• 1 sdm daun bawang, untuk taburan",

                "• 100 g daging ayam cincang",
                "• 50 g wortel, cincang halus",
                "• 1 sdm tepung tapioca",
                "• 2 siung bawang putih, cincang halus",
                "• 1⁄2 sdt kaldu ayam bubuk",
                "• 2 sdm SAORI® Saus Teriyaki",
                "• 1 sdm minyak goreng",
                "• 50 ml air",

                "• 6 butir bawang merah, haluskan",
                "• 3 butir kemiri, haluskan",
                "• 4 butir cengkeh",
                "• 5 cm kayu manis",
                "• 1 lembar daun salam",
                "• 2 siung bawang putih, haluskan",
                "• 1 cm jahe iris",
                "• ½ sdt merica putih bubuk",
                "• ½ biji pala"
            )
        )
    }

    fun getStepsList(): List<List<String>> {
        return listOf(
            listOf(
                "Panaskan minyak lalu tumis bawang putih.",
                "Kemudian masukkan udang tumis dan sebentar lalu tambahkan jamur kuping, wortel, dan kubis. Aduk rata.",
                "Tambahkan lada putih, Masako® Kaldu Jamur, dan air lalu aduk rata kembali, sisihkan.",
                "Siapkan kulit lumpia, beri isian lumpia lalu gulung dan rekatkan dengan air.",
                "Goreng lumpia hingga matang dan berwarna kecokelatan.",
                SAJIKAN
            ), listOf(
                "Panaskan minyak kemudian tumis bawang putih, tomat, dan daun bawang hingga harum.",
                "Tambahkan air, AJI-NO-MOTO®, dan garam. Masak sampai tomat lunak.",
                "Masukkan tahu, lalu masak kembali sampai mendidih.",
                "Tuang telur yang sudah dikocok lalu aduk perlahan.",
                SAJIKAN
            ), listOf(
                "Haluskan ayam, udang, putih telur, minyak wijen, kecap asin, garam, gula pasir, lada putih, AJI-NO-MOTO®, tepung sagu menggunakan blender.",
                "Setelah halus, masukkan bawang goreng dan daun bawang. Aduk menggunakan spatula.",
                "Potong cakwe menjadi 3 bagian, gunting bagian tengahnya.",
                "Isi cakwe dengan adonan ayam udang.",
                "Goreng cakwe hingga matang.",
                "Sajikan dengan Mayumi® Pedas."
            ), listOf(
                "Masukkan bawang merah, bawang putih, cabai rawit merah, cabai merah keriting, dan minyak ke dalam blender. Haluskan.",
                "Tumis bumbu hingga harum dan matang.",
                "Tambahkan telur, aduk hingga rata.",
                "Masukkan sosis dan nasi, aduk rata.",
                "Tambahkan garam, Masako® Ayam, dan Ajinomoto® Terasi Udang.",
                SAJIKAN
            ), listOf(
                "Rebus dada ayam hingga matang, lalu suwir.",
                "Campurkan bawang merah, cabai rawit, serai, dan daun jeruk.",
                "Tambahkan AJI-NO-MOTO®, garam, gula, terasi, dan perasan jeruk nipis.",
                "Panaskan minyak kelapa, tambahkan ke campuran sambal, aduk rata.",
                "Campur ayam suwir dengan sambal.",
                SAJIKAN
            ), listOf(
                "Campur bawang merah, bawang putih, ketumbar, kecap inggris, lengkuas, gula merah, baking powder, garam, lada putih, dan telur. Blender hingga halus.",
                "Potong daging slice dan marinasi selama 30 menit.",
                "Lumuri dengan Sajiku® Tepung Bumbu Serbaguna Pedas.",
                "Goreng hingga kecokelatan.",
                SAJIKAN
            ), listOf(
                "Potong sawi putih dari batang, kukus hingga layu.",
                "Campur ayam cincang, bawang putih, daun bawang, wortel, jahe, minyak wijen, dan Masako® Light. Aduk rata.",
                "Bungkus dengan sawi.",
                "Kukus cabbage dumpling selama 15 menit.",
                "Sajikan dengan chilli oil."
            ), listOf(
                "Marinasi daging dengan bawang bombay, bawang putih, daun bawang, SAORI® Bulgogi, wijen sangrai, dan wortel selama 10 menit.",
                "Tumis daging dan mie yang sudah direbus.",
                "Tambahkan SAORI® Bulgogi.",
                "Sajikan dengan garnish daun bawang dan wijen."
            ), listOf(
                "Campur daging ayam, jagung manis, kentang, Sajiku® Ayam Goreng, telur, dan maizena. Aduk rata.",
                "Tuang ke loyang dan kukus 15 menit.",
                "Potong segitiga, lapisi dengan telur, gulingkan ke tepung panir.",
                "Goreng hingga keemasan.",
                "Sajikan dengan pelengkap."
            ), listOf(
                "Keruk tahu untuk tempat isi, campur sisa tahu dengan daging ayam, wortel, bawang putih, dan SAORI® Saus Teriyaki.",
                "Isi tahu dengan adonan.",
                "Tumis tahu isi dengan sedikit minyak hingga matang.",
                "Tambahkan kaldu, SAORI® Saus Teriyaki, dan gula merah.",
                "Masukkan tahu, kentang, dan wortel.",
                SAJIKAN
            )
        )
    }
}