project(
    'Pythonator',
    'c',
    default_options: [
        'c_std=gnu11',
        'b_lto=true',
        'buildtype=minsize',
    ]
)

if not meson.is_cross_build()
    error('Pythonator controller should be cross-compiled')
endif

add_global_arguments(
    [
        '-ffunction-sections',
        '-fdata-sections',
        '-Wall',
        '-Wextra',
        '-DBAUD_TOL=3' # Silence warnings about baud rates
    ],
    language: 'c'
)

add_global_link_arguments(
    [
        '-ffunction-sections',
        '-fdata-sections',
        '-fuse-linker-plugin',
        '-Wl,--gc-sections,--relax'
    ],
    language: 'c'
)

sources = [
    'src/main.c',
    'src/serial.c',
    'src/stepper.c',
    'src/pen.c'
]

pythonator_exe = executable(
    'pythonator.elf',
    sources,
    build_by_default: true
)

pythonator_ihx = custom_target(
    'pythonator',
    input: pythonator_exe,
    output: 'pythonator.ihx',
    build_by_default: true,
    command: [
        find_program('objcopy'),
        '-O', 'ihex',
        '-R', '.eeprom',
        '@INPUT@',
        '@OUTPUT@'
    ]
)

run_target(
    'flash',
    command: [
        find_program('avrdude'),
        '-p', host_machine.cpu(),
        '-c', meson.get_cross_property('programmer'),
        '-b', meson.get_cross_property('baud_rate'),
        '-D',
        '-U', 'flash:w:@0@:i'.format(pythonator_ihx.full_path()),
        '-P', get_option('port')
    ],
    depends: pythonator_ihx
)