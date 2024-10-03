@extends('layout.main')

<head>
<link rel="icon" type="image/x-icon" href="{{ asset('logo-bawaslu.png') }}"/>
</head>
@section('content')
    <x-breadcrumb
        :values="[__('menu.transaction.menu'), __('menu.transaction.incoming_letter')]">
        <a href="{{ route('transaction.incoming.create') }}" class="btn btn-primary">{{ __('menu.general.create') }}</a>
    </x-breadcrumb>

    @foreach($data as $letter)
        <x-letter-card
            :letter="$letter"
        />
    @endforeach

    {!! $data->appends(['search' => $search])->links() !!}
@endsection
