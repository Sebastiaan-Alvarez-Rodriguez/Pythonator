project(
    'Pythonator-simulator',
    'cpp',
    default_options: [
        'cpp_std=c++17',
        'b_lto=true',
        'buildtype=debugoptimized',
    ]
)

sources = [
    'src/main.cpp',
    'src/graphics/Shader.cpp',
    'src/Renderer.cpp'
]

add_project_arguments(
    [
        '-Wconversion',
        '-Wall',
        '-Wextra',
        '-Wno-unused-parameter',
        '-DGLFW_INCLUDE_NONE'
    ],
    language: 'cpp'
)

dependencies = [
    dependency('gl'),
    dependency('glfw3'),
    subproject('glad').get_variable('glad_dep')
]

executable(
    'pythonatorsim',
    sources,
    dependencies: dependencies,
    include_directories: include_directories('src')
)
