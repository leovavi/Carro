package carro.game;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Input;

public class Carro implements ApplicationListener {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public ModelBatch modelBatch;
	public AssetManager assets;
	public Environment environment;
	public boolean loading;
	public float rotation_x = 0f;
	public float rotation_y = 0f;
	public float rotation_z = 0f;
	public float velocity = 0.0f;
	public float acceleration = 0.0f;
	ModelInstance my_model;

	@Override
	public void create () {
		modelBatch = new ModelBatch();
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(7f, 7f, 7f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		camController = new CameraInputController(cam);
		//Gdx.input.setInputProcessor(camController);

		assets = new AssetManager();
		assets.load("convCarro.g3db", Model.class);
		loading = true;
	}

	@Override
	public void render () {
		if (loading && assets.update())
		{
			Model ship = assets.get("convCarro.g3db", Model.class);
			my_model = new ModelInstance(ship);
			loading = false;
		}
		//camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		modelBatch.begin(cam);
		if(my_model!=null)
		{
			modelBatch.render(my_model, environment);
			float current_position_x = my_model.transform.getTranslation(new Vector3()).x;
			float current_position_y = my_model.transform.getTranslation(new Vector3()).y;
			float current_position_z = my_model.transform.getTranslation(new Vector3()).z;

			if(Gdx.input.isKeyPressed(Input.Keys.UP))
				acceleration = -0.01f;
			else if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
				acceleration = 0.01f;
			else{
				acceleration = 0f;
				velocity = 0f;
			}
			
			velocity+=acceleration;
			
			float delta_z = (float)Math.sin(Math.toRadians(rotation_y))*velocity;
			float delta_x = (float)Math.cos(Math.toRadians(rotation_y))*velocity;
			current_position_z -= delta_z;
			current_position_x += delta_x;
			
			if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
				rotation_y+=1f;
			if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
				rotation_y-=1f;
			
			if(rotation_y>360 || rotation_y<0)
				rotation_y%=360;
			
			my_model.transform.setToTranslation(current_position_x, current_position_y, current_position_z);
			
			my_model.transform.rotate(Vector3.X, rotation_x);
			my_model.transform.rotate(Vector3.Y, rotation_y);
			my_model.transform.rotate(Vector3.Z, rotation_z);
		}
		modelBatch.end();
	}

	@Override
	public void dispose () {
		modelBatch.dispose();
		assets.dispose();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}