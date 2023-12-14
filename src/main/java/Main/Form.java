/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Usuario
 */
public class Form extends javax.swing.JFrame {

    private ButtonGroup promoteButtonGroup;
    
    public Form() {
        initComponents();
        setLocationRelativeTo(null);
        
         promoteButtonGroup = new ButtonGroup();

        
        promoteButtonGroup.add(promote);
        promoteButtonGroup.add(noPromote);
    }


    @SuppressWarnings("unchecked")
    
    //________________________________________________________________________________________________
    //Crear
    public void agregar(String aNombre, String aApellido, Boolean promote) {
    // Validar que los campos no sean nulos o vacíos
    if (aNombre == null || aNombre.isEmpty() || aApellido == null || aApellido.isEmpty() || promote == null) {
        JOptionPane.showMessageDialog(null, "Todos los campos son obligatorios");
        return; // Salir del método si algún campo es nulo o vacío
    }

    //Definir la sentencia sql para insertar un alumno con un nombre proporcionado
    String sql = "INSERT INTO alumnos (nombre, apellido, promociona) VALUES(?, ?, ?)";

    //Creamos la instancia de la clase Main para establecer la conexión a la base de datos
    Main con = new Main();

    //Establecemos la conexión
    Connection conexion = con.establecerConeccion();

    try {
        PreparedStatement preparedStatement = conexion.prepareStatement(sql);

        preparedStatement.setString(1, aNombre);
        preparedStatement.setString(2, aApellido);
        preparedStatement.setBoolean(3, promote);

        //Ejecutar la sentencia SQL y obtener el número de filas afectadas
        int filasAfectadas = preparedStatement.executeUpdate();

        //Comprobar si se agregó o no el registro en nuestro software
        if (filasAfectadas > 0) {
            JOptionPane.showMessageDialog(null, "Alumno agregado exitosamente");
        } else {
            JOptionPane.showMessageDialog(null, "No se ha podido agregar al alumno");
        }

        //Cerramos la declaración preparada
        preparedStatement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    //_______________________________________________________________________________________________
    public void mostrar() {
        String sql = "SELECT * FROM alumnos";

        //Creamos una instancia de la clase main para establecer la conexion a la base de datos
        Main con = new Main();

        Connection conexion = con.establecerConeccion();

        System.out.println(sql);

        //Creamos un modelo de tabla para almacenar los datos
        DefaultTableModel model = new DefaultTableModel();

        try {

            //Creamos una declaración para ejecutar la consulta SQL
            Statement st = conexion.createStatement();

            ResultSet rs = st.executeQuery(sql);

            //Obtenemos la información de las colunmas de la consulta
            ResultSetMetaData metaData = rs.getMetaData();

            int numColumnas = metaData.getColumnCount();

            for (int column = 1; column <= numColumnas; column++) {
                model.addColumn(metaData.getColumnName(column));
            }

            //Agregamos las filas al modelo de la tabla
            while (rs.next()) {
                Object[] rowData = new Object[numColumnas];
                for (int i = 0; i < numColumnas; i++) {
                    //Obtenemos los datos de cada columna por indice (comience en 1)
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            //Asignamos el modelo de tabla al componente TablaDatos
            JOptionPane.showMessageDialog(null, "La tabla se visualiza de manera correcta");
            TablaDatos.setModel(model);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "La tabla no se pudo visualizar");
        }

    }

    //_________________________________________________________________________________________
    //Actualizar
    //Obtenemos el id seleccionado
    public int obtenerIdSeleccionado() {
        //obtener la fila seleccionada en la tabla
        int filaSeleccionada = TablaDatos.getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(null, "Debe seleccionar una fila");
            return -1; //Retornamos un valor negativo para indicar que no se ha seleccionado nada
        }
        int id = (int) TablaDatos.getValueAt(filaSeleccionada, 0);
        return id;
    }

    public void modificar() {
    // Obtener los nuevos datos del alumno desde los campos de texto
    String nuevoNombre = aNombre.getText();
    String nuevoApellido = aApellido.getText();
    boolean nuevoPromociona = promote.isSelected();

    // Verificar si se proporcionaron nuevos datos
    if (nuevoNombre.isEmpty() || nuevoApellido.isEmpty()) {
        JOptionPane.showMessageDialog(null, "Debe ingresar nueva información del Alumno");
    } else {
        Main con = new Main();

        Connection conexion = con.establecerConeccion();

        if (conexion != null) {
            try {
                // Obtener el ID del alumno seleccionado en la tabla
                int idSeleccionado = obtenerIdSeleccionado();

                if (idSeleccionado != -1) {
                    String sql = "UPDATE alumnos SET nombre = ?, apellido = ?, promociona = ? WHERE id = ?";

                    PreparedStatement preparedStatement = conexion.prepareStatement(sql);

                    preparedStatement.setString(1, nuevoNombre);
                    preparedStatement.setString(2, nuevoApellido);
                    preparedStatement.setBoolean(3, nuevoPromociona);
                    preparedStatement.setInt(4, idSeleccionado);

                    // Ejecutar la actualización
                    int filasAfectadas = preparedStatement.executeUpdate();

                    if (filasAfectadas > 0) {
                        // La actualización fue exitosa
                        JOptionPane.showMessageDialog(null, "Datos del alumno actualizados exitosamente");
                    } else {
                        JOptionPane.showMessageDialog(null, "No se pudo modificar los datos del alumno");
                    }

                    // Cerrar la sentencia preparada
                    preparedStatement.close();
                } else {
                    JOptionPane.showMessageDialog(null, "No se pudo modificar los datos del alumno");
                }

                // Cerrar la conexión
                conexion.close();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al modificar los datos del alumno");
            }
        } else {
            JOptionPane.showMessageDialog(null, "No se pudo establecer la conexión");
        }
    }
}

    //___________________________________________________________________________________________________________________
    
    //Eliminar
    public void eliminar(){
        int filaSeleccionada = TablaDatos.getSelectedRow();
        
        if(filaSeleccionada == -1){
            JOptionPane.showMessageDialog(null, "Debe seleccionar una fila");
        }else{
            int idEliminar = (int) TablaDatos.getValueAt(filaSeleccionada, 0);
            
            String sql = "DELETE FROM alumnos WHERE id = " + idEliminar;
            
            try{
                Main con = new Main();
                
                Connection conexion = con.establecerConeccion();
                
                Statement st = conexion.createStatement();
                
                int filasAfectadas = st.executeUpdate(sql);
                
                if(filasAfectadas > 0){
                    JOptionPane.showMessageDialog(null, "El alumno fue borrado con exito");
                }else{
                    JOptionPane.showMessageDialog(null, "No se pudo borrar el alumno");
                }
                st.close();
                conexion.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
    
    //__________________________________________________________________________________________________________________

    public void nuevo() {
        aNombre.setText("");
        aNombre.requestFocus();
    }

    //_______________________________________________________________________________
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaDatos = new javax.swing.JTable();
        btnDelete = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnCreate = new javax.swing.JButton();
        btnRead = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        idAlumno = new javax.swing.JTextField();
        aApellido = new javax.swing.JTextField();
        aNombre = new javax.swing.JTextField();
        promote = new javax.swing.JRadioButton();
        noPromote = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TablaDatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Nombre", "Apellido", "Promociona"
            }
        ));
        TablaDatos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TablaDatosMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TablaDatos);

        btnDelete.setText("Eliminar");
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnUpdate.setText("Editar");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnCreate.setText("Agregar");
        btnCreate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateActionPerformed(evt);
            }
        });

        btnRead.setText("Ver Lista");
        btnRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReadActionPerformed(evt);
            }
        });

        jLabel1.setText("ID:");

        jLabel2.setText("Nombre:");

        jLabel3.setText("Apellido:");

        jLabel4.setText("Promociona:");

        aApellido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aApellidoActionPerformed(evt);
            }
        });

        promote.setText("Promociona");
        promote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                promoteActionPerformed(evt);
            }
        });

        noPromote.setText("No Promociona");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(promote)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(noPromote))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(aApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 649, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(aApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(promote)
                    .addComponent(noPromote))
                .addGap(45, 45, 45)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRead, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCreate, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aApellidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aApellidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_aApellidoActionPerformed

    private void promoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_promoteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_promoteActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        modificar();
        mostrar();
        nuevo();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnCreateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateActionPerformed
        String nombre = aNombre.getText();
        String apellido = aApellido.getText();
        boolean promociona = promote.isSelected();
        agregar(nombre, apellido, promociona);
        mostrar();
        nuevo();
    }//GEN-LAST:event_btnCreateActionPerformed

    private void btnReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReadActionPerformed
        mostrar();
    }//GEN-LAST:event_btnReadActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        eliminar();
        mostrar();
        nuevo();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void TablaDatosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TablaDatosMouseClicked
        int fila = TablaDatos.getSelectedRow();

    if (fila == -1) {
        JOptionPane.showMessageDialog(null, "Alumno no seleccionado");
    } else {
        int id = Integer.parseInt(TablaDatos.getValueAt(fila, 0).toString());
        String nombre = (String) TablaDatos.getValueAt(fila, 1);
        String apellido = (String) TablaDatos.getValueAt(fila, 2);
        boolean promociona = (boolean) TablaDatos.getValueAt(fila, 3);

        idAlumno.setText("" + id);
        aNombre.setText(nombre);
        aApellido.setText(apellido);
        promote.setSelected(promociona);
    }
    }//GEN-LAST:event_TablaDatosMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable TablaDatos;
    private javax.swing.JTextField aApellido;
    private javax.swing.JTextField aNombre;
    private javax.swing.JButton btnCreate;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnRead;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JTextField idAlumno;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JRadioButton noPromote;
    private javax.swing.JRadioButton promote;
    // End of variables declaration//GEN-END:variables
}
