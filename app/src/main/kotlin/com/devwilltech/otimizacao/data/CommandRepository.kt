package com.devwilltech.otimizacao.data

// ─── Domain models ────────────────────────────────────────────────────────────

data class AdbCommand(
    val name: String,
    val command: String,
    val category: String
)

data class CommandResult(
    val command: AdbCommand,
    val success: Boolean,
    val output: String = ""
)

// ─── Repository ───────────────────────────────────────────────────────────────

object CommandRepository {

    val optimisationCommands: List<AdbCommand> = listOf(

        // ════════════════════════════════════════════════════════════════════
        // SISTEMA  (20 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Zerar animação janelas",          "settings put global window_animation_scale 0",                                                         "Sistema"),
        AdbCommand("Zerar animação transição",        "settings put global transition_animation_scale 0",                                                     "Sistema"),
        AdbCommand("Zerar duração animação",          "settings put global animator_duration_scale 0",                                                        "Sistema"),
        AdbCommand("Forçar UI hardware",              "settings put global force_hw_ui_enabled 1",                                                            "Sistema"),
        AdbCommand("Limitar procs background 2",     "settings put global background_process_limit 2",                                                       "Sistema"),
        AdbCommand("Manter activities vivas",         "settings put global always_finish_activities 0",                                                       "Sistema"),
        AdbCommand("Desativar modo economia",         "settings put global low_power 0",                                                                     "Sistema"),
        AdbCommand("Desativar sugestões rede",        "settings put global network_recommendations_enabled 0",                                                "Sistema"),
        AdbCommand("Desativar app standby",           "settings put global app_standby_enabled 0",                                                           "Sistema"),
        AdbCommand("Desativar multi-CB",              "settings put global multi_cb_smart_selection 0",                                                      "Sistema"),
        AdbCommand("Desativar smart battery",         "settings put global smart_battery_enabled 0",                                                         "Sistema"),
        AdbCommand("Brilho tela máximo",              "settings put system screen_brightness 255",                                                           "Sistema"),
        AdbCommand("Brilho manual (sem auto)",        "settings put system screen_brightness_mode 0",                                                        "Sistema"),
        AdbCommand("Desativar night display",         "settings put secure night_display_activated 0",                                                       "Sistema"),
        AdbCommand("Desativar acessibilidade daltônico", "settings put secure accessibility_display_daltonizer -1",                                         "Sistema"),
        AdbCommand("Desativar backup automático",     "bmgr enable false 2>/dev/null || true",                                                               "Sistema"),
        AdbCommand("Desativar heads-up notificações", "settings put global heads_up_notifications_enabled 0",                                                "Sistema"),
        AdbCommand("Desativar avisos de canal",       "settings put global show_notification_channel_warnings 0",                                            "Sistema"),
        AdbCommand("Modo imersivo FF Normal",         "settings put global policy_control immersive.status=com.dts.freefireth:immersive.navigation=com.dts.freefireth", "Sistema"),
        AdbCommand("Modo imersivo FF MAX",            "settings put global policy_control immersive.status=com.dts.freefiremax:immersive.navigation=com.dts.freefiremax", "Sistema"),

        // ════════════════════════════════════════════════════════════════════
        // GRÁFICOS  (25 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Renderização GPU forçada",        "settings put global hardware_accelerated_rendering 1",                                                "Gráficos"),
        AdbCommand("HWUI dirty regions off",          "setprop debug.hwui.render_dirty_regions false",                                                       "Gráficos"),
        AdbCommand("HWUI overdraw off",               "setprop debug.hwui.show_overdraw false",                                                              "Gráficos"),
        AdbCommand("Renderer Skia GL",                "setprop debug.renderengine.backend skiagl",                                                           "Gráficos"),
        AdbCommand("SF vsync debug off",              "setprop debug.sf.showupdates 0",                                                                      "Gráficos"),
        AdbCommand("SurfaceFlinger perf mode",        "service call SurfaceFlinger 1008 i32 1 2>/dev/null || true",                                          "Gráficos"),
        AdbCommand("Desativar sombras GPU",           "setprop debug.hwui.disable_draw_shadow true",                                                         "Gráficos"),
        AdbCommand("Ativar triple buffer SF",         "setprop debug.sf.use_phase_offsets_as_durations 1",                                                   "Gráficos"),
        AdbCommand("EGL GPU trace off",               "setprop debug.egl.traceGpuCompletion 0",                                                              "Gráficos"),
        AdbCommand("Persist HW aceleração",           "setprop persist.sys.ui.hw 1",                                                                         "Gráficos"),
        AdbCommand("Ativar HW compositor",            "setprop debug.sf.hw 1",                                                                               "Gráficos"),
        AdbCommand("Desativar blur UI",               "setprop persist.sys.sf.disable_blurs 1",                                                              "Gráficos"),
        AdbCommand("Forçar OpenGL ES acelerado",      "setprop debug.egl.hw 1",                                                                              "Gráficos"),
        AdbCommand("HWUI profile off",                "setprop debug.hwui.profile false",                                                                    "Gráficos"),
        AdbCommand("HWUI render thread on",           "setprop debug.hwui.use_render_thread true",                                                           "Gráficos"),
        AdbCommand("GPU pixel buffers off",           "setprop debug.hwui.use_gpu_pixel_buffers false",                                                      "Gráficos"),
        AdbCommand("SF latch unsignaled",             "setprop debug.sf.latch_unsignaled 1",                                                                 "Gráficos"),
        AdbCommand("SF latência mínima",              "setprop debug.sf.enable_gl_backpressure 0",                                                           "Gráficos"),
        AdbCommand("Desativar VDS SF",                "setprop debug.sf.enable_hwc_vds 0",                                                                   "Gráficos"),
        AdbCommand("Modo alta resolução SF",          "setprop ro.surface_flinger.max_frame_buffer_acquired_buffers 3",                                       "Gráficos"),
        AdbCommand("Forçar 90 fps (suporte)",         "settings put system peak_refresh_rate 90.0 2>/dev/null || true",                                      "Gráficos"),
        AdbCommand("Forçar 120 fps (suporte)",        "settings put system peak_refresh_rate 120.0 2>/dev/null || true",                                     "Gráficos"),
        AdbCommand("Frame rate mínimo 60",            "settings put system min_refresh_rate 60.0 2>/dev/null || true",                                       "Gráficos"),
        AdbCommand("Desativar round corners anim",    "setprop persist.sys.overlay.decouple 1 2>/dev/null || true",                                          "Gráficos"),
        AdbCommand("HWUI layer cache zero",           "setprop debug.hwui.layer_cache_size 0",                                                               "Gráficos"),

        // ════════════════════════════════════════════════════════════════════
        // TOUCH  (10 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Desativar haptic feedback",       "settings put system haptic_feedback_enabled 0",                                                       "Touch"),
        AdbCommand("Desativar vibração toque",        "settings put system vibrate_on_touch 0",                                                              "Touch"),
        AdbCommand("Long press timeout mínimo",       "settings put secure long_press_timeout 100",                                                          "Touch"),
        AdbCommand("Multi-touch predict on",          "setprop persist.sys.touch.predict 1",                                                                 "Touch"),
        AdbCommand("Touch boost CPU on",              "setprop sys.perf.touchboost 1 2>/dev/null || true",                                                   "Touch"),
        AdbCommand("Velocidade ponteiro máxima",      "settings put system pointer_speed 7",                                                                 "Touch"),
        AdbCommand("Desativar IME auto-show",         "settings put secure show_ime_with_hard_keyboard 0",                                                   "Touch"),
        AdbCommand("Desativar keyboard suggestions",  "settings put secure input_method_selector_visibility 0 2>/dev/null || true",                          "Touch"),
        AdbCommand("Touch latency mínima",            "setprop persist.sys.touch.min_latency 0 2>/dev/null || true",                                         "Touch"),
        AdbCommand("Desativar teclado vibracao",      "settings put system keyboard_vibration_enabled 0 2>/dev/null || true",                                "Touch"),

        // ════════════════════════════════════════════════════════════════════
        // REDE  (15 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("DNS Cloudflare principal",        "ndc resolver setnetdns wifi '' 1.1.1.1 1.0.0.1 2>/dev/null || true",                                  "Rede"),
        AdbCommand("DNS Google principal",            "ndc resolver setnetdns wifi '' 8.8.8.8 8.8.4.4 2>/dev/null || true",                                  "Rede"),
        AdbCommand("Desativar WiFi scan throttle",   "settings put global wifi_scan_throttle_enabled 0",                                                    "Rede"),
        AdbCommand("WiFi MAC random off",             "settings put global wifi_connected_mac_randomization_enabled 0",                                      "Rede"),
        AdbCommand("WiFi sleep policy off",           "settings put global wifi_sleep_policy 2",                                                             "Rede"),
        AdbCommand("Mobile data always on off",       "settings put global mobile_data_always_on 0",                                                         "Rede"),
        AdbCommand("TCP low latency on",              "sysctl -w net.ipv4.tcp_low_latency=1 2>/dev/null || true",                                            "Rede"),
        AdbCommand("Buffer TCP receber 8MB",          "sysctl -w net.core.rmem_max=8388608 2>/dev/null || true",                                             "Rede"),
        AdbCommand("Buffer TCP enviar 8MB",           "sysctl -w net.core.wmem_max=8388608 2>/dev/null || true",                                             "Rede"),
        AdbCommand("TCP fast open 3",                 "sysctl -w net.ipv4.tcp_fastopen=3 2>/dev/null || true",                                               "Rede"),
        AdbCommand("TCP timestamps off",              "sysctl -w net.ipv4.tcp_timestamps=0 2>/dev/null || true",                                             "Rede"),
        AdbCommand("TCP congestion BBR",              "sysctl -w net.ipv4.tcp_congestion_control=bbr 2>/dev/null || true",                                   "Rede"),
        AdbCommand("Desativar Private DNS",           "settings put global private_dns_mode off 2>/dev/null || true",                                        "Rede"),
        AdbCommand("Backlog TCP máximo",              "sysctl -w net.ipv4.tcp_max_syn_backlog=4096 2>/dev/null || true",                                     "Rede"),
        AdbCommand("Sockets UDP buffer",              "sysctl -w net.core.netdev_max_backlog=4096 2>/dev/null || true",                                      "Rede"),

        // ════════════════════════════════════════════════════════════════════
        // BATERIA  (10 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Whitelist doze FF Normal",        "cmd deviceidle whitelist +com.dts.freefireth",                                                        "Bateria"),
        AdbCommand("Whitelist doze FF MAX",           "cmd deviceidle whitelist +com.dts.freefiremax",                                                       "Bateria"),
        AdbCommand("Desativar battery saver",         "settings put global low_power 0",                                                                     "Bateria"),
        AdbCommand("Desativar adaptive battery",      "settings put global app_standby_enabled 0",                                                           "Bateria"),
        AdbCommand("Ignorar otim. bateria FF Normal", "dumpsys deviceidle whitelist +com.dts.freefireth 2>/dev/null || true",                                "Bateria"),
        AdbCommand("Ignorar otim. bateria FF MAX",    "dumpsys deviceidle whitelist +com.dts.freefiremax 2>/dev/null || true",                               "Bateria"),
        AdbCommand("Simular carregador AC",           "dumpsys battery set ac 1 2>/dev/null || true",                                                        "Bateria"),
        AdbCommand("Simular bateria 100%",            "dumpsys battery set level 100 2>/dev/null || true",                                                   "Bateria"),
        AdbCommand("Desativar doze global",           "settings put global device_idle_enabled 0 2>/dev/null || true",                                       "Bateria"),
        AdbCommand("Thermal restrict off",            "setprop persist.vendor.thermal.config 0 2>/dev/null || true",                                         "Bateria"),

        // ════════════════════════════════════════════════════════════════════
        // MEMÓRIA  (20 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Matar procs background (am)",     "am kill-all",                                                                                         "Memória"),
        AdbCommand("Drop caches kernel",              "sync && echo 3 > /proc/sys/vm/drop_caches 2>/dev/null || true",                                       "Memória"),
        AdbCommand("VM swappiness 10",                "sysctl -w vm.swappiness=10 2>/dev/null || true",                                                      "Memória"),
        AdbCommand("VM cache pressure 50",            "sysctl -w vm.vfs_cache_pressure=50 2>/dev/null || true",                                              "Memória"),
        AdbCommand("Compact memory",                  "echo 1 > /proc/sys/vm/compact_memory 2>/dev/null || true",                                            "Memória"),
        AdbCommand("OOM killer allocating off",       "sysctl -w vm.oom_kill_allocating_task=0 2>/dev/null || true",                                         "Memória"),
        AdbCommand("Zram enabled",                    "setprop persist.sys.zram_enabled 1 2>/dev/null || true",                                              "Memória"),
        AdbCommand("Freeze cached apps",              "settings put global cached_apps_freezer enabled 2>/dev/null || true",                                 "Memória"),
        AdbCommand("Fechar app telefone",             "am force-stop com.android.phone 2>/dev/null || true",                                                 "Memória"),
        AdbCommand("Fechar assistente Google",        "am force-stop com.google.android.googlequicksearchbox 2>/dev/null || true",                           "Memória"),
        AdbCommand("Fechar GMS unstable",             "am force-stop com.google.android.gms.unstable 2>/dev/null || true",                                   "Memória"),
        AdbCommand("Fechar Play Store",               "am force-stop com.android.vending 2>/dev/null || true",                                               "Memória"),
        AdbCommand("Dalvik heap start 8m",            "setprop dalvik.vm.heapstartsize 8m",                                                                  "Memória"),
        AdbCommand("Dalvik heap size 256m",           "setprop dalvik.vm.heapsize 256m",                                                                     "Memória"),
        AdbCommand("Dalvik growth limit 128m",        "setprop dalvik.vm.heapgrowthlimit 128m",                                                              "Memória"),
        AdbCommand("Dirty ratio VM 10",               "sysctl -w vm.dirty_ratio=10 2>/dev/null || true",                                                     "Memória"),
        AdbCommand("Dirty background ratio 5",        "sysctl -w vm.dirty_background_ratio=5 2>/dev/null || true",                                           "Memória"),
        AdbCommand("Fechar Samsung Game Launcher",    "am force-stop com.samsung.android.game.gamehome 2>/dev/null || true",                                 "Memória"),
        AdbCommand("Fechar Bixby",                    "am force-stop com.samsung.android.bixby.agent 2>/dev/null || true",                                   "Memória"),
        AdbCommand("Fechar Xiaomi services idle",     "am force-stop com.miui.msa.global 2>/dev/null || true",                                               "Memória"),

        // ════════════════════════════════════════════════════════════════════
        // DESEMPENHO CPU/IO  (25 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("CPU governor performance",
            "for f in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo performance > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("CPU freq máxima mínima",
            "for f in /sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq; do cat \${f/min/max} > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("GPU freq máxima (Adreno)",
            "for f in /sys/class/kgsl/kgsl-3d0/devfreq/min_freq; do cat \${f/min/max} > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("GPU governor performance (Adreno)",
            "echo performance > /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null || true",
            "Desempenho"),
        AdbCommand("GPU powerlevel mínimo",
            "echo 0 > /sys/class/kgsl/kgsl-3d0/min_pwrlevel 2>/dev/null || true",
            "Desempenho"),
        AdbCommand("Compilar FF Normal speed",        "cmd package compile -m speed com.dts.freefireth 2>/dev/null || true",                                 "Desempenho"),
        AdbCommand("Compilar FF MAX speed",           "cmd package compile -m speed com.dts.freefiremax 2>/dev/null || true",                                "Desempenho"),
        AdbCommand("Recompilar dex FF Normal",        "cmd package bg-dexopt-job 2>/dev/null || true",                                                       "Desempenho"),
        AdbCommand("I/O scheduler deadline",
            "for f in /sys/block/*/queue/scheduler; do echo deadline > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("I/O read ahead 512kb",
            "for f in /sys/block/*/queue/read_ahead_kb; do echo 512 > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("I/O nr_requests 256",
            "for f in /sys/block/*/queue/nr_requests; do echo 256 > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("Thermal vendor off",              "setprop persist.vendor.overlay.thermal 0 2>/dev/null || true",                                        "Desempenho"),
        AdbCommand("Thermal QTI disable",             "setprop persist.vendor.qti.irqbalance 0 2>/dev/null || true",                                         "Desempenho"),
        AdbCommand("Game mode FF Normal (2)",         "cmd game set --mode 2 com.dts.freefireth 2>/dev/null || true",                                        "Desempenho"),
        AdbCommand("Game mode FF MAX (2)",            "cmd game set --mode 2 com.dts.freefiremax 2>/dev/null || true",                                       "Desempenho"),
        AdbCommand("Top app render boost",            "setprop persist.sys.perf.topAppRenderThreadBoost.enable true 2>/dev/null || true",                    "Desempenho"),
        AdbCommand("Input thread boost",              "setprop debug.perf.force_cpu_set 0 2>/dev/null || true",                                              "Desempenho"),
        AdbCommand("IRQ sched balance off",           "setprop debug.sf.enable_hwc_vds 0",                                                                   "Desempenho"),
        AdbCommand("IOrap readahead on",              "cmd device_config put runtime_native_boot iorap_readahead_enable true 2>/dev/null || true",            "Desempenho"),
        AdbCommand("Selinux permissive",              "setenforce 0 2>/dev/null || true",                                                                    "Desempenho"),
        AdbCommand("Slow boot disable",               "setprop persist.sys.disable_rescue false 2>/dev/null || true",                                        "Desempenho"),
        AdbCommand("CPU online todos",
            "for f in /sys/devices/system/cpu/cpu*/online; do echo 1 > \$f 2>/dev/null; done; true",
            "Desempenho"),
        AdbCommand("Huge pages madvise",              "sysctl -w vm.nr_hugepages=0 2>/dev/null || true",                                                     "Desempenho"),
        AdbCommand("Sched RT runtime unlimit",        "sysctl -w kernel.sched_rt_runtime_us=-1 2>/dev/null || true",                                         "Desempenho"),
        AdbCommand("Kernel perf bias 0",
            "for f in /sys/devices/system/cpu/cpu*/power/energy_perf_bias; do echo 0 > \$f 2>/dev/null; done; true",
            "Desempenho"),

        // ════════════════════════════════════════════════════════════════════
        // JOGO — Free Fire Específico  (25 comandos)
        // ════════════════════════════════════════════════════════════════════
        AdbCommand("Prioridade foreground FF Normal", "am set-process-importance com.dts.freefireth foreground",                                             "Jogo"),
        AdbCommand("Prioridade foreground FF MAX",    "am set-process-importance com.dts.freefiremax foreground",                                            "Jogo"),
        AdbCommand("Whitelist doze deviceidle FF",   "cmd deviceidle whitelist +com.dts.freefireth 2>/dev/null || true",                                    "Jogo"),
        AdbCommand("Whitelist doze deviceidle FFM",  "cmd deviceidle whitelist +com.dts.freefiremax 2>/dev/null || true",                                   "Jogo"),
        AdbCommand("Limpar cache FF Normal",          "pm clear --cache-only com.dts.freefireth 2>/dev/null || true",                                        "Jogo"),
        AdbCommand("Limpar cache FF MAX",             "pm clear --cache-only com.dts.freefiremax 2>/dev/null || true",                                       "Jogo"),
        AdbCommand("Desativar ANR background",        "settings put global anr_show_background 0",                                                           "Jogo"),
        AdbCommand("Desativar dropbox crash",         "settings put global dropbox:data_app_crash 0",                                                        "Jogo"),
        AdbCommand("Force-stop concorrentes",         "am force-stop com.tencent.ig 2>/dev/null; am force-stop com.pubg.krmobile 2>/dev/null; true",         "Jogo"),
        AdbCommand("Desativar atualização OTA",       "settings put global ota_disable_automatic_update 1 2>/dev/null || true",                              "Jogo"),
        AdbCommand("Ativar modo jogo FF Normal",      "settings put global game_mode com.dts.freefireth 1 2>/dev/null || true",                              "Jogo"),
        AdbCommand("Ativar modo jogo FF MAX",         "settings put global game_mode com.dts.freefiremax 1 2>/dev/null || true",                             "Jogo"),
        AdbCommand("FF Normal sem restrição bateria", "dumpsys battery unplug 2>/dev/null; cmd power set-adaptive-power-saver-enabled false 2>/dev/null; true", "Jogo"),
        AdbCommand("Bloquear notificações durante jogo", "settings put global heads_up_notifications_enabled 0",                                             "Jogo"),
        AdbCommand("Silenciar notificações",          "settings put global notification_policy_access_packages '' 2>/dev/null || true",                      "Jogo"),
        AdbCommand("Desativar safe-mode reboot",      "setprop persist.sys.boot.reason gaming 2>/dev/null || true",                                          "Jogo"),
        AdbCommand("Refresh rate FF Normal",
            "cmd game set --fps com.dts.freefireth 90 2>/dev/null || true",
            "Jogo"),
        AdbCommand("Refresh rate FF MAX",
            "cmd game set --fps com.dts.freefiremax 90 2>/dev/null || true",
            "Jogo"),
        AdbCommand("Fixar tarefas FF Normal foreground",
            "am task lock \$(am stack list 2>/dev/null | grep com.dts.freefireth | head -n1 | awk '{print \$2}' | tr -d ':') 2>/dev/null || true",
            "Jogo"),
        AdbCommand("NN API aceleração FF",            "setprop debug.nn.use-driver-handles-capabilities 1 2>/dev/null || true",                              "Jogo"),
        AdbCommand("Desativar crash dialog",          "setprop debug.appsec.report_crash 0 2>/dev/null || true",                                             "Jogo"),
        AdbCommand("Latência áudio mínima",           "setprop af.fast_track_multiplier 1 2>/dev/null || true",                                              "Jogo"),
        AdbCommand("AudioFlinger performance",        "setprop ro.audio.flinger_standbytime_ms 0 2>/dev/null || true",                                       "Jogo"),
        AdbCommand("Desativar NFC durante jogo",      "svc nfc disable 2>/dev/null || true",                                                                 "Jogo"),
        AdbCommand("Desativar Location durante jogo", "settings put secure location_mode 0 2>/dev/null || true",                                             "Jogo"),

    )

    // ─── Comando de reset (restaura padrões) ─────────────────────────────────
    val resetCommand: AdbCommand = AdbCommand(
        name     = "Restaurar Padrões do Sistema",
        category = "Reset",
        command  = """
            settings put global window_animation_scale 1
            settings put global transition_animation_scale 1
            settings put global animator_duration_scale 1
            settings put global background_process_limit 0
            settings put global low_power 0
            settings put global app_standby_enabled 1
            settings put global heads_up_notifications_enabled 1
            settings put global smart_battery_enabled 1
            settings put system haptic_feedback_enabled 1
            settings put system vibrate_on_touch 1
            settings put system screen_brightness_mode 1
            settings put secure long_press_timeout 500
            settings put global network_recommendations_enabled 1
            settings put global policy_control null
            cmd deviceidle whitelist -com.dts.freefireth 2>/dev/null || true
            cmd deviceidle whitelist -com.dts.freefiremax 2>/dev/null || true
            dumpsys battery reset 2>/dev/null || true
            svc nfc enable 2>/dev/null || true
            settings put secure location_mode 3 2>/dev/null || true
        """.trimIndent()
    )
}
