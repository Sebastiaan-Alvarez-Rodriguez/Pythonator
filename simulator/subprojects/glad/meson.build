project(
    'glad',
    'cpp',
    version: '3.3',
    default_options: [
        'cpp_std=c++17',
    ]
)

cxx = meson.get_compiler('cpp')
dl_dep = cxx.find_library('dl', required: false)

glad_dep = declare_dependency(
    include_directories: include_directories('include'),
    sources: 'src/glad.cpp',
    dependencies: dl_dep
)
