class ImageNotFoundException extends Exception {
    ImageNotFoundException(ImageProducer source) {
	super(source+"");
    }
}

