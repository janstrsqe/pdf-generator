package com.example.demo.service.impl.report;

import com.example.demo.dto.in.RequestPDFData;
import com.example.demo.service.IncidentReportService;
import com.example.demo.service.impl.report.pdf.IncidentPDFReport;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class IncidentReportServiceImpl implements IncidentReportService {

    private final IncidentPDFReport incidentPDFReport;

    public void generateIncidentPDFReport() {
        RequestPDFData dto = new RequestPDFData(
                "Unit Test 123",
                Instant.parse("2025-11-10T08:30:00Z"),
                new RequestPDFData.Location(
                        "Dimana aja", "S 01°42'15\" E 117°28'33\"", "+125 meter dari permukaan laut", "Level 5 (RL +125m)",
                        new RequestPDFData.EnvironmentCondition(32, 15, "Cerah, Tidak Hujan", "Baik (>10 km)\n", "Kering, tidak licin"), ""),
                "Kategori Insiden",
                "Test Ganti Title",
                "1 Operator, 2 Mekanik",
                "Pada tanggal 15 Oktober 2024 pukul 14:35 WIB, terjadi kegagalan sistem hidrolik pada unit Excavator HD 785-7 dengan nomor unit EX-127 yang sedang beroperasi di area Pit 3 Selatan, Lati Mine. Insiden terjadi saat operator sedang melakukan aktivitas penggalian material overburden.\n" +
                        "Kegagalan sistem hidrolik menyebabkan boom excavator turun secara tiba-tiba dan tidak terkontrol, mengakibatkan bucket menghantam area kerja dengan keras. Tidak ada korban jiwa atau luka-luka dalam insiden ini, namun unit mengalami kerusakan signifikan pada sistem hidrolik dan struktur boom.",
                "Pada tanggal 15 Oktober 2024, operator Budi Santoso memulai shift kedua pada pukul 13:45 WIB dan melakukan pemeriksaan pre-operation check pada unit Excavator EX-127 di area Pit 3 Selatan. Seluruh pemeriksaan visual menunjukkan kondisi unit dalam keadaan normal dan layak operasi.\n" +
                        "Unit mulai beroperasi pada pukul 14:00 WIB untuk melakukan aktivitas penggalian material overburden. Selama kurang lebih 20 menit pertama, operasi berjalan dengan normal tanpa ada indikasi masalah. Namun pada pukul 14:20 WIB, operator mulai mendengar suara tidak normal yang berasal dari area sistem hidrolik unit, meskipun semua fungsi operasional masih berjalan dengan baik.\n" +
                        "Operator segera melaporkan kondisi tersebut kepada supervisor melalui radio komunikasi pada pukul 14:25 WIB. Supervisor memberikan instruksi untuk menyelesaikan cycle penggalian yang sedang berlangsung dan kemudian menghentikan unit untuk dilakukan pemeriksaan lebih lanjut. Operator melanjutkan operasi dengan kehati-hatian ekstra sambil menunggu penyelesaian cycle tersebut.\n" +
                        "Pada pukul 14:35 WIB, terjadi kegagalan sistem hidrolik secara mendadak. Boom excavator turun dengan tiba-tiba dan tidak terkontrol, menyebabkan bucket menghantam area kerja dengan keras. Operator segera mematikan engine dan mengaktifkan emergency stop pada pukul 14:36 WIB, kemudian melakukan evakuasi dari unit dengan selamat.\n" +
                        "Pada pukul 14:38 WIB, operator melaporkan insiden ke control room dan tim emergency response segera dimobilisasi. Tim emergency response tiba di lokasi pada pukul 14:42 WIB dan langsung melakukan isolasi area serta pemasangan barricade untuk keamanan. Tim maintenance dan safety mulai melakukan inspeksi awal pada pukul 14:50 WIB, dilanjutkan dengan proses investigasi dan dokumentasi lengkap mulai pukul 15:15 WIB. Unit kemudian dipindahkan ke workshop pada pukul 16:00 WIB untuk investigasi dan perbaikan lebih detail.",
                "Kegagalan seal hydraulic cylinder pada boom excavator yang menyebabkan kebocoran internal dan kehilangan tekanan hidrolik secara mendadak. Seal kit telah melewati masa pakai standar (6 bulan overdue) dan unit telah beroperasi 8,500 jam tanpa overhaul sistem hidrolik.\n" +
                        "Berdasarkan investigasi mendalam, akar penyebab utama insiden ini adalah kegagalan sistematis dalam manajemen preventive maintenance. Jadwal penggantian seal kit hydraulic cylinder yang seharusnya dilakukan setiap 6 bulan atau 4,000 jam operasi (mana yang lebih dulu) tidak diikuti dengan ketat.\n" +
                        "Unit EX-127 tercatat telah melewati due date penggantian seal kit selama 6 bulan, namun tidak ada mekanisme automatic stop atau eskalasi yang memaksa unit untuk dihentikan. Prioritas terhadap target produksi mengakibatkan jadwal maintenance sering ditunda atau diabaikan, dengan asumsi bahwa unit masih dapat beroperasi selama tidak ada tanda-tanda kerusakan yang jelas.\n" +
                        "Selain itu, sistem monitoring kondisi unit masih bersifat manual dan reaktif. Tidak ada sensor tekanan hidrolik real-time yang dapat mendeteksi penurunan performa sistem sebelum terjadi kegagalan total. Operator yang mendengar suara tidak normal sejak 15 menit sebelum insiden tidak mengambil keputusan untuk segera menghentikan unit, karena masih mengikuti instruksi untuk menyelesaikan cycle operasi.\n" +
                        "Faktor budaya kerja juga berperan, dimana terdapat tekanan implisit untuk \"complete the cycle\" demi memenuhi target produksi.",
                new RequestPDFData.Summary(0, 1, 72));

        incidentPDFReport.generateIncidentReportPDF(dto);
    }

}
