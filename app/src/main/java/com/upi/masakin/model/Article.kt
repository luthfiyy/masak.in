package com.upi.masakin.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Article(
    val id: Int,
    val title: String,
    val author: String,
    val category: ArtikelCategory,
    val publishDate: String,
    val readTime: Int,
    val thumbnailUrl: String,
    val content: String,
    val tags: List<String>,
    val shortDescription: String
) : Parcelable

enum class ArtikelCategory {
    CookingTips,       // Tips Memasak
    ChefProfile,       // Profil Chef
    Nutrition,         // Gizi dan Kesehatan
    KitchenTools,      // Review Peralatan
    CulinaryCulture,   // Budaya Kuliner
    SpecialRecipe,     // Resep Khusus
    IngredientGuide;   // Panduan Bahan

    fun toReadableString(): String {
        return name.replace(Regex("([a-z])([A-Z])"), "$1 $2")
    }
}


val sampleArtikels = listOf(
    Article(
        id = 1,
        title = "Mengenal Keel: Bagian Ayam yang Lezat dan Bergizi\n",
        author = "Fardi Rizal",
        category = ArtikelCategory.CookingTips,
        publishDate = "13 Nov 2024",
        readTime = 7,
        thumbnailUrl = "https://cdn0-production-images-kly.akamaized.net/_q3Tmeg1orot6zB9GxOEo9JJNxo=/1280x720/smart/filters:quality(75):strip_icc():format(webp)/kly-media-production/medias/5001700/original/002446000_1731384135-keel-adalah-bagian-ayam.jpg",
        content = "Artikel lengkap tentang teknik memasak dasar...",
        tags = listOf("memasak", "pemula", "tips"),
        shortDescription = "Pelajari tentang keel, bagian ayam yang lezat dan bergizi. Temukan informasi lengkap mulai dari definisi, manfaat, hingga cara memasak"
    ), Article(
        id = 2,
        title = "Baby Octopus Adalah: Panduan Lengkap Mengenal Hidangan Laut Mungil nan Lezat",
        author = "Liputan 6",
        category = ArtikelCategory.ChefProfile,
        publishDate = "12 Nov 2024",
        readTime = 5,
        thumbnailUrl = "https://cdn0-production-images-kly.akamaized.net/uVU7US2kat8sQljjlMWB6KlEItg=/640x360/smart/filters:quality(75):strip_icc():format(webp)/kly-media-production/medias/5001425/original/003559900_1731382652-baby-octopus-adalah.jpg",
        content = "Baby octopus tidak hanya lezat, tetapi juga menawarkan berbagai manfaat kesehatan. Berikut adalah beberapa keuntungan mengonsumsi baby octopus:\n" + "\n" + "- Sumber Protein Berkualitas Tinggi\n" + "Baby octopus kaya akan protein lengkap yang mengandung semua asam amino esensial. Protein ini penting untuk pembentukan dan perbaikan jaringan tubuh, serta mendukung fungsi sistem kekebalan.\n" + "\n" + "- Rendah Kalori dan Lemak\n" + "Dengan kandungan kalori dan lemak yang rendah, baby octopus menjadi pilihan yang baik bagi mereka yang sedang menjalani program penurunan berat badan atau menjaga pola makan sehat.\n" + "\n" + "- Kaya Mineral Penting\n" + "Baby octopus mengandung mineral seperti zat besi, magnesium, fosfor, dan selenium. Zat besi penting untuk produksi sel darah merah, sementara magnesium mendukung kesehatan jantung dan sistem saraf.\n" + "\n" + "- Sumber Vitamin B12\n" + "Vitamin B12 yang terkandung dalam baby octopus berperan penting dalam pembentukan sel darah merah dan menjaga kesehatan sistem saraf.\n" + "\n" + "- Mengandung Asam Lemak Omega-3\n" + "Meskipun kadar lemaknya rendah, baby octopus mengandung asam lemak omega-3 yang bermanfaat untuk kesehatan jantung dan otak.",
        tags = listOf("chef", "memasak", "tips"),
        shortDescription = "Pelajari semua tentang baby octopus, dari definisi hingga cara memasaknya. Temukan manfaat, tips memilih, dan resep lezat hidangan gurita mini ini."
    ), Article(
        id = 3,
        title = "Cara Buat Nasi Goreng yang Lezat dan Praktis, Lengkap dengan Teknik dan Variasinya",
        author = "Liputan6",
        category = ArtikelCategory.ChefProfile,
        publishDate = "12 Nov 2024",
        readTime = 5,
        thumbnailUrl = "https://cdn0-production-images-kly.akamaized.net/2cET6xbKRnJJeGfXdxFW3e_ClDI=/640x360/smart/filters:quality(75):strip_icc():format(webp)/kly-media-production/medias/4982818/original/087743900_1730099439-cara-buat-nasi-goreng.jpg",
        content = "Untuk membuat nasi goreng yang lezat, pemilihan bahan dasar yang berkualitas sangatlah penting. Berikut adalah bahan-bahan dasar yang umumnya digunakan dalam pembuatan nasi goreng:\n" + "\n" + "1. Nasi: Idealnya, gunakan nasi yang sudah dingin atau nasi sisa dari hari sebelumnya. Nasi yang baru dimasak cenderung terlalu lembek dan basah, yang dapat menghasilkan nasi goreng yang lengket dan kurang nikmat.\n" + "2. Minyak goreng: Pilih minyak goreng yang berkualitas baik. Minyak sayur atau minyak kelapa sawit adalah pilihan yang umum digunakan.\n" + "3. Telur: Telur ayam segar memberikan tambahan protein dan tekstur yang lezat pada nasi goreng.\n" + "4. Sayuran: Wortel, kacang polong, jagung, atau buncis dapat ditambahkan untuk meningkatkan nilai gizi dan memberikan warna pada nasi goreng.\n" + "5. Protein hewani: Ayam, udang, atau daging sapi cincang sering digunakan sebagai tambahan protein dalam nasi goreng.\n" + "6. Bawang putih dan bawang merah: Kedua jenis bawang ini merupakan dasar aromatis yang penting dalam pembuatan nasi goreng.\n" + "7. Cabai: Untuk menambah rasa pedas, cabai merah atau cabai rawit sering digunakan.\n" + "8. Kecap manis: Kecap manis memberikan warna cokelat yang khas dan rasa manis pada nasi goreng.",
        tags = listOf("chef", "memasak", "tips"),
        shortDescription = "Pelajari cara buat nasi goreng yang lezat dan praktis dengan berbagai variasi resep. Temukan tips dan trik untuk membuat nasi goreng sempurna di rumah."
    )
)