@extends('layout.main')

<head>
<link rel="icon" type="image/x-icon" href="{{ asset('logo-bawaslu.png') }}"/>
</head>

@section('content')
    <x-breadcrumb
        :values="[__('menu.gallery.menu'), __('menu.gallery.incoming_letter')]">
    </x-breadcrumb>

    <div class="row row-cols-1 row-cols-md-3 g-4 mb-5">
        @foreach($data as $attachment)
        <div class="col">
            <x-gallery-card
                :filename="$attachment->filename"
                :extension="$attachment->extension"
                :path="$attachment->path_url"
                :letter="$attachment->letter"
            />
        </div>
        @endforeach
    </div>


    {!! $data->appends(['search' => $search])->links() !!}

@endsection
