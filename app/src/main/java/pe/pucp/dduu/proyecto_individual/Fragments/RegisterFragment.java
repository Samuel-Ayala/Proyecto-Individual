package pe.pucp.dduu.proyecto_individual.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import pe.pucp.dduu.proyecto_individual.ActivitiesGeneral.LoginRegistroActivity;
import pe.pucp.dduu.proyecto_individual.Entity.Usuario;
import pe.pucp.dduu.proyecto_individual.R;

public class RegisterFragment extends Fragment {

    public RegisterFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    View vista;
    Button btnCancelar, btnRegistro;
    EditText txtUser, txtPwd, txtNames, txtCodigo, txtNamesAlumno, txtDni, txtCelular;
    Spinner txtGrado,txtSeccion;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.fragment_register, container, false);
        txtUser = (EditText) vista.findViewById(R.id.inputCorreoRegistro);
        txtDni = (EditText) vista.findViewById(R.id.inputDni);
        txtCelular = (EditText) vista.findViewById(R.id.inputNumeroCelular);
        txtPwd = (EditText) vista.findViewById(R.id.inputPasswordRegistro);
        txtNames= (EditText) vista.findViewById(R.id.inputNombre);
        txtNamesAlumno = (EditText) vista.findViewById(R.id.inputNombreEstudiante);
        txtCodigo = (EditText) vista.findViewById(R.id.inputCodigo);
        txtGrado = (Spinner) vista.findViewById(R.id.spinnerGrados);
        txtSeccion = (Spinner) vista.findViewById(R.id.spinnerSecciones);
        btnCancelar = (Button) vista.findViewById(R.id.buttonCancelarRegistro);
        btnRegistro = (Button) vista.findViewById(R.id.buttonAceptarRegistro);

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciar_sesion();
            }
        });

        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = txtNames.getText().toString();
                String email = txtUser.getText().toString();
                String contra = txtPwd.getText().toString();
                String dni = txtDni.getText().toString();
                String celular = txtCelular.getText().toString();
                String nameEstudiante = txtNamesAlumno.getText().toString();
                String rol = "apoderado";
                String codigo = txtCodigo.getText().toString();
                String grado = txtGrado.getSelectedItem().toString();
                String seccion = txtSeccion.getSelectedItem().toString();

                Usuario usuario = new Usuario();
                usuario.setNombre(name);
                usuario.setCorreo(email);
                usuario.setRol(rol);
                usuario.setCodigo(codigo);
                usuario.setDni(dni);
                usuario.setNumeroCelular(celular);
                usuario.setNombreEstudiante(nameEstudiante);
                usuario.setGradoEstudiante(grado);
                usuario.setSeccionEstudiante(seccion);

                if (name.isEmpty() || email.isEmpty() || contra.isEmpty() || dni.isEmpty() || celular.isEmpty() || nameEstudiante.isEmpty()
                    || rol.isEmpty() || codigo.isEmpty() || grado.isEmpty() || seccion.isEmpty()) {
                    LoginRegistroActivity m2 = (LoginRegistroActivity) getActivity();
                    m2.errorDeRegistro();
                }else if (codigo.replaceAll("\\s","").length() > 8 || codigo.replaceAll("\\s","").length() < 8){
                    LoginRegistroActivity m2 = (LoginRegistroActivity) getActivity();
                    m2.errorDeCodigo();
                }else if (dni.replaceAll("\\s","").length() > 8 || dni.replaceAll("\\s","").length() < 8){
                    LoginRegistroActivity m2 = (LoginRegistroActivity) getActivity();
                    m2.errorDeDni();
                } else {
                    LoginRegistroActivity m2 = (LoginRegistroActivity) getActivity();
                    m2.registro(usuario, contra);
                }

            }
        });
        return vista;
    }

    void iniciar_sesion() {
        LoginFragment fr=new LoginFragment();
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.contenedor_login_registro,fr)
                .addToBackStack(null)
                .commit();
    }
}